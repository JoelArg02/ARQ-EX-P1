#!/bin/bash

# Función para esperar a que MySQL esté disponible
wait_for_mysql() {
  echo "Esperando a que MySQL esté disponible en $1..."
  for i in {1..30}; do
    if nc -z $1 3306 2>/dev/null; then
      echo "MySQL está listo!"
      sleep 2  # Espera adicional para asegurar que MySQL esté completamente inicializado
      return 0
    fi
    echo "Intento $i/30: MySQL no está listo aún..."
    sleep 2
  done
  echo "ERROR: MySQL no estuvo disponible después de 60 segundos"
  return 1
}

# Función para esperar a que Payara esté listo
wait_for_payara() {
  echo "Esperando a que Payara esté listo..."
  for i in {1..60}; do
    if ${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile list-domains 2>/dev/null | grep -q "domain1 running"; then
      echo "Payara está listo!"
      sleep 3  # Espera adicional para asegurar que esté completamente inicializado
      return 0
    fi
    echo "Intento $i/60: Payara no está listo aún..."
    sleep 3
  done
  echo "ERROR: Payara no estuvo disponible después de 180 segundos"
  return 1
}

# Esperar a que MySQL esté disponible
wait_for_mysql "mysql_comercializadora"

# Iniciar Payara en background
${PAYARA_DIR}/bin/asadmin start-domain &

# Esperar que Payara esté realmente listo
wait_for_payara

# Cambiar puertos para evitar conflicto con el otro Payara
${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile set configs.config.server-config.http-service.http-listener.http-listener-1.port=8081
${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile set configs.config.server-config.admin-service.jmx-connector.system.port=8687
${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile set configs.config.server-config.iiop-service.iiop-listener.orb-listener-1.port=3801
${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile set configs.config.server-config.iiop-service.iiop-listener.SSL.port=3921
${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile set configs.config.server-config.iiop-service.iiop-listener.SSL_MUTUALAUTH.port=3961

# Configurar JDBC Connection Pool
${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile create-jdbc-connection-pool \
  --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource \
  --restype javax.sql.DataSource \
  --property user=root:password=root:serverName=mysql_comercializadora:portNumber=3306:databaseName=comercializadora_db:useSSL=false:allowPublicKeyRetrieval=true \
  ComercializadoraPool 2>/dev/null || echo "Pool already exists"

# Crear JDBC Resource
${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile create-jdbc-resource \
  --connectionpoolid ComercializadoraPool \
  java:/comercializadoraDS 2>/dev/null || echo "Resource already exists"

# Ping para verificar
${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile ping-connection-pool ComercializadoraPool

# Desplegar aplicación WAR
echo "Desplegando aplicación Comercializadora..."
${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile deploy --force=true ${DEPLOY_DIR}/*.war

echo "=== Datasource Comercializadora configurado y aplicación desplegada ==="

# Detener Payara y esperar a que realmente se detenga
echo "Deteniendo Payara para reinicio limpio..."
${PAYARA_DIR}/bin/asadmin stop-domain

# Esperar a que el dominio se detenga completamente
for i in {1..10}; do
  if ! ${PAYARA_DIR}/bin/asadmin list-domains 2>/dev/null | grep -q "domain1 running"; then
    echo "Dominio detenido completamente"
    break
  fi
  echo "Esperando que el dominio se detenga... ($i/10)"
  sleep 2
done

# Espera adicional para asegurar que los puertos se liberen
sleep 3

# Mantener contenedor activo
echo "Iniciando Payara en modo watchdog..."
exec ${PAYARA_DIR}/bin/asadmin start-domain --verbose --watchdog
