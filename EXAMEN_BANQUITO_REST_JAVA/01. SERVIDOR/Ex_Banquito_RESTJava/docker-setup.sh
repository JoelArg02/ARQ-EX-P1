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
wait_for_mysql "mysql_banquito"

# Iniciar Payara en background
${PAYARA_DIR}/bin/asadmin start-domain &

# Esperar que Payara esté realmente listo
wait_for_payara

# Configurar JDBC Connection Pool (SIN cambiar puertos, usa los default 8080/4848)
${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile create-jdbc-connection-pool \
  --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource \
  --restype javax.sql.DataSource \
  --property user=root:password=root:serverName=mysql_banquito:portNumber=3306:databaseName=banquito_db:useSSL=false:allowPublicKeyRetrieval=true \
  BanquitoPool 2>/dev/null || echo "Pool already exists"

# Crear JDBC Resource
${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile create-jdbc-resource \
  --connectionpoolid BanquitoPool \
  java:/banquitoDS 2>/dev/null || echo "Resource already exists"

# Ping para verificar
${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile ping-connection-pool BanquitoPool

# Desplegar aplicación WAR
echo "Desplegando aplicación Banquito..."
${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile deploy --force=true ${DEPLOY_DIR}/*.war

echo "=== Datasource Banquito configurado y aplicación desplegada ==="

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
