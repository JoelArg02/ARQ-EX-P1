#!/bin/bash

# Iniciar Payara en background
${PAYARA_DIR}/bin/asadmin start-domain --verbose=false &

# Esperar que esté listo
sleep 15

# Desplegar aplicación
echo "Desplegando Cliente Web..."
${PAYARA_DIR}/bin/asadmin --user admin --passwordfile=/opt/payara/passwordFile deploy --force=true ${DEPLOY_DIR}/*.war

echo "=== Cliente Web desplegado exitosamente ==="

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
