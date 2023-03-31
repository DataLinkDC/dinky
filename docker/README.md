# Docker build Documentation

## Description of the directory

```shell
├── docker      -- docker build folder
│   ├── app     -- flink docker
│   ├── mysql   -- mysql docker build
│   └── server  -- dinky-server build
│   └── web     -- dinky-web build
```

## Separate server and web deployment

### dinky-server

maven build dinky-server

```shell
# build flink1.14,no web
mvn clean install -Dmaven.test.skip=true -P pord,scala-2.12,flink-1.14,!web

# build flink1.15,no web
mvn clean install -Dmaven.test.skip=true -P pord,scala-2.12,flink-1.15,!web
```

#### no flink jar build

> **Recommended**
>
> This method builds an image that does not contain the Flink jar package, and the runtime simply maps the directory/opt/dinky-docker/plugins to the host and then adds the Flink jar package to the host

Execute the `docker build` in the project root

```shell
docker build -t dinky-server_flink14:0.8.0 -f ./docker/server/Dockerfile ./
```

docker run

> The database address and file-mapping directory can be modified by itself

```shell
docker run \
-d \
--restart=always \
-p 8888:8888 \
-p 8081:8081  \
-e MYSQL_ADDR=${YOUR_MYSQL_ADDR} \
-e MYSQL_DATABASE=${YOUR_MYSQL_DATABASE} \
-e MYSQL_USERNAME=${YOUR_MYSQL_USERNAME} \
-e MYSQL_PASSWORD=${YOUR_MYSQL_PASSWORD} \
-v /opt/dinky-docker/plugins:/opt/dinky/plugins \
-v /opt/dinky-docker/logs:/opt/dinky/logs \
--name dinky-server_flink1.14 \
dinky-server_flink1.14:0.8.0
```

Download the Flink tar package yourself and place the jar package under the Lib Directory of the Flink file under the host map directory
this example is `/opt/dinky-docker/plugins/flink1.14`


#### flink jar deploy

> This method deployment, which pulls Flink jars from the internet every time a Docker build is built,
> causes the Docker build to be slow,
> or is it recommended that Dinky deploy jars without Flink when Dinky Docker is running,
> import jar packages for file mapping

This location comment is removed under the `./docker/server/Dockerfile`,change the version of Flink to the version you want

```shell
# download flink file
ADD https://archive.apache.org/dist/flink/flink-1.14.6/flink-1.14.6-bin-scala_2.12.tgz /tmp
RUN tar zxvf /tmp/flink-1.14.6-bin-scala_2.12.tgz -C /opt
RUN cp -r /opt/flink-1.14.6/lib /opt/dinky/plugins/flink${FLINK_VERSION}
```
docker build
```shell
# 在项目根目录下执行
docker build -t dinky-server:0.8.0 -f ./docker/server/Dockerfile ./
```

run dinky-server

> The file mapping address and the database address are modified by themselves

```shell
docker run \
-d \
--restart=always \
-p 8888:8888 \
-p 8081:8081  \
-e MYSQL_ADDR=${YOUR_MYSQL_ADDR} \
-e MYSQL_DATABASE=${YOUR_MYSQL_DATABASE} \
-e MYSQL_USERNAME=${YOUR_MYSQL_USERNAME} \
-e MYSQL_PASSWORD=${YOUR_MYSQL_PASSWORD} \
-v /opt/dinky-docker/plugins:/opt/dinky/plugins \
-v /opt/dinky-docker/logs:/opt/dinky/logs \
--name dinky-server \
dinky-server:0.8.0
```

### web deploy

> version：
> * nodejs: 14.17.0+
> * npm: 7.19.0
>
> update npm version `npm install npm@7.19.0 -g`
>
> **Execute under the `flink-web` Directory of the project**

npm install

```shell
npm install --force
```

npm build

```shell
npm run build
```
After the build, the `dist` directory will appear in the web directory

docker build web, execute at the project root

```shell
docker run \
-d \
--restart=always \
-p 80:80 \
-e API_ORIGIN=${YOUR_DINKY_SERVER_API}\
--name dinky-web \
dinky-web:0.8.0
```
> `API_ORIGIN` is `dinky-server` host

## dinky deploy

maven build Dinky server

```shell
# build flink1.14,no web
mvn clean install -Dmaven.test.skip=true -P pord,scala-2.12,flink-1.14

# build flink1.15,no web
mvn clean install -Dmaven.test.skip=true -P pord,scala-2.12,flink-1.15
```

Execute `docker build` at the project root

```shell
docker build -t dinky-server_flink14:0.8.0 -f ./docker/Dockerfile ./
```

run dinky-server

> The file mapping address and the database address are modified by themselves

```shell
docker run \
-d \
--restart=always \
-p 8888:8888 \
-e MYSQL_ADDR=${YOUR_MYSQL_ADDR} \
-e MYSQL_DATABASE=${YOUR_MYSQL_DATABASE} \
-e MYSQL_USERNAME=${YOUR_MYSQL_USERNAME} \
-e MYSQL_PASSWORD=${YOUR_MYSQL_PASSWORD} \
-v /opt/dinky-docker/plugins:/opt/dinky/plugins \
-v /opt/dinky-docker/logs:/opt/dinky/logs \
--name dinky-server \
dinky-server:0.8.0
```