[![Build Status](https://travis-ci.org/khorshidi/tree-api.svg?branch=master)](https://travis-ci.org/khorshidi/tree-api)
[![codecov](https://codecov.io/gh/khorshidi/tree-api/branch/master/graph/badge.svg)](https://codecov.io/gh/khorshidi/tree-api)
# tree-api
A simple RESTful API for Tree manipulation

### Technologies
- Java 8
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Lombok](https://projectlombok.org)
- [Redis](https://redis.io/)
- [Redisson](https://github.com/redisson/redisson)
- [JUnit](https://junit.org/)

### REST api
| METHOD | PATH | Description | Parameters | 
| -----------| ------ | ------ | ----- |
| GET | /nodes/{id} | one node | - | |
| GET | /nodes/{id}/children | all children of node | - | 
| PATCH | /nodes/{id} | change parent | parentId : new parent Id |

### How to build
```sh
./mvnw clean package
```
### How to run
```sh
docker-compose build
docker-compose up
```
open [http://localhost:8080](http://localhost:8080)

![](screen.gif)

(using [JSONView](https://chrome.google.com/webstore/detail/jsonview/chklaanhfefbnpoihckbnefhakgolnmc?hl=en) extention for google-chrome) 
### How to run tests
```sh
mvn clean test
# converage file: ./target/site/jacoco/index.htmldocker-compose up
# be sure that 8080 and 6379 ports are not in use, before building or running testes.
``` 

### Tree representation in Redis
Four key-set in Redis:

* 'R'  &rarr; '{rootId}'
* 'P:{nodeId}'  &rarr; '{parentId}' 
* 'C:{nodeId}'  &rarr; Set of children ids
* 'H:{nodeId}'  &rarr; Height of Node

for example:
```
(1) 
 │
 ├──(2)
 │
 └──(3)
     ├── (4)
     └── (5)
 
``` 
is represented by:

R &rarr; 1                  
                            
P:2 &rarr; 1                
P:3 &rarr; 1                
P:4 &rarr; 3                
P:5 &rarr; 3                
                            
C:1 &rarr; <2, 3> \
C:3 &rarr; <4, 5> 

H:1 &rarr; 0 \
H:2 &rarr; 1 \
H:3 &rarr; 1 \
H:4 &rarr; 2 \
H:5 &rarr; 2

### ```.env``` config file
By changing `.env` file and re-running `docker-compose up` you can change startup tree.  
##### config for random tree at startup
```config
RANDOM_TREE=true
TREE_FROM_FILE=false
TREE_FILE_PATH=null
```
##### config for no tree at startup (use previous tree in redis)
```config
RANDOM_TREE=false
TREE_FROM_FILE=false
TREE_FILE_PATH=null
```
##### config for tree from file
```config
RANDOM_TREE=false
TREE_FROM_FILE=true
TREE_FILE_PATH=/data/<filename.csv>
```  
example `file.csv`:
```csv
1 ,null
2 ,1
3 ,1
4 ,3
5 ,3
```
(copy your file to /var/lib/docker/volumes/tree-api_tree-data/_data)
