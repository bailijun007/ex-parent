call mvn clean:clean
call mvn deploy -X -Dmaven.test.skip=true
@pause