@REM ASSUMES project checked out at C:\DEV\medialib
@REM Also see postman
@SET slash=%%5C
@SET colon=%%3A
@REM relative path resolved from project root
@SET fpath=src%slash%test%slash%resources%slash%testdata
curl -o - -i -X PUT http://127.0.0.1:8080/api/v1/directory?path=%fpath%
curl -o - -i -X GET http://127.0.0.1:8080/api/v1/directory?path=%fpath%%slash%A
curl -o - -i -X PUT http://127.0.0.1:8080/api/v1/directory?path=%fpath%%slash%A
curl -o - -i -X GET http://127.0.0.1:8080/api/v1/directory?path=%fpath%%slash%A
curl -o - -i -X GET http://127.0.0.1:8080/api/v1/directory?path=%fpath%%slash%MISSING