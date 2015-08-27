//socket.c
//作者：王振

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/wait.h>

#define DEBUG_HTTP 1
#define DEBUG_HTTP_RECV 1
int Connect(int fd, struct sockaddr *addr, socklen_t len);

int Socket_Connect(char *ip, char *port);

/*
功能:向socketfd发送数据,内部实现了循环发送len长度
参数:
sockfd 是代表你与远程程序连接的套接字描述符。
msg 是一个指针，指向你想发送的信息的地址。
len 是你想发送信息的长度。
flags 发送标记。一般都设为0
返回:
0-------- 成功
退出---失败
修改:
备注:
王振
*/
int Send(int sockfd, char *sendbuf, int len, int flags);

int Close(int sockfd);
int Recv(int sockfd, char *recvbuf, int len, int flags);