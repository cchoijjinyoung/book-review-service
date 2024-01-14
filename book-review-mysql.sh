sudo docker run -d \
  --name book-review-mysql \
  -e MYSQL_ROOT_PASSWORD=7777asd \
  -e MYSQL_DATABASE=book-review \
  -e MYSQL_USER=topy \
  -e MYSQL_PASSWORD=7777asd \
  -p 3306:3306 \
  mysql:latest