docker run -d \
  --name book-review-mysql \
  -e MYSQL_ROOT_PASSWORD=1234 \
  -e MYSQL_DATABASE=book-review \
  -e MYSQL_USER=topy \
  -e MYSQL_PASSWORD=1234 \
  -p 3306:3306 \
  mysql:latest