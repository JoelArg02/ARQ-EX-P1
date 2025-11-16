1️⃣ COPIAR el archivo SQL dentro del contenedor

➡️ Ubícate en la carpeta donde está el archivo:
01. SERVIDOR\Ex_Comercializadora_RESTJava

Ejecuta:

docker cp database_script.sqlmysql-db:/database_script.sql

✅ 2️⃣ ENTRAR al contenedor y ejecutar MySQL

docker exec -it mysql-db bash

mysql -uroot -p1234 < /database_script.sql

Y ya...