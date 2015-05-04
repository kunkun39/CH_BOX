phone_control端包括工具包utils,socket包，和界面包ott,
ott:
MainActivity 和yuyinActivity为两个 activity界面，intentphoneReciver暂时无用。

socket:
boxSocketInterface包括拿给clinentSocketClient发送和clientSocketServer心跳维持 共享的接口和全局共享的状态。
clinentSocketClient 为发送数据的类
clientSocketServer 心跳维持的类
romoteInfoContenter 为soket的一些数据结构。

utils:
property为android属性读取类 而inputManager为其中的一个属性。使用的时候要一起移植。
stringUtils为字符串处理类