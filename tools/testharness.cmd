@REM ASSUMES project checked out at C:\DEV\medialib
@REM Also see postman
@SET slash=%%5c
@SET colon=%%3a
@SET fpath=C%colon%%slash%DEV%slash%medialib%slash%tools%slash%testdata
curl -o - -i -X PUT http://127.0.0.1:8080/api/v1/directory?path=%fpath%
curl -o - -i -X GET http://127.0.0.1:8080/api/v1/directory?path=%fpath%
curl -o - -i -X GET http://127.0.0.1:8080/api/v1/directory?path=%fpath%%slash%MISSING