[![Build Status](https://travis-ci.org/letusfly85/play-silhouette-scalikejdbc-for-js.svg?branch=master)](https://travis-ci.org/letusfly85/play-silhouette-scalikejdbc-for-js)

# PlayFramework silhouette seed project with ScalikeJDBC

## MySQL

```bash
docker run --name example \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=example \
  -p 3306:3306 \
  -d mysql \
  --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```
