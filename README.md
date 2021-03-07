# jwt_token
Simple token authentication demo based on JWT and springboot.

利用JWT实现的简单token认证demo，后端使用SpringBoot。
---
主要流程可如下表示：</br>

登录并请求token：
1. client登录，向server请求token
2. server验证登录信息，生成JWT token和refresh token。
3. client接受token，存入localstorage

请求验证：
1. client将token放入请求头headers并发送
2. server将请求在对应拦截器中进行处理，检查token是否过期
3. 三种情况：token过期，返回登陆页面；token未过期，正常请求继续；token过期，refresh token未过期，生成新token与refresh token并放入response的header中，之后正常回应。
4. client接收正常回应，并检查响应头中是否有token信息，有则更新local storage中信息。
---
验证部分功能由拦截器实现
