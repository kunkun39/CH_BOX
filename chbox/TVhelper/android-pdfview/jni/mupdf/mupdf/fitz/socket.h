//socket.c
//���ߣ�����

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
����:��socketfd��������,�ڲ�ʵ����ѭ������len����
����:
sockfd �Ǵ�������Զ�̳������ӵ��׽�����������
msg ��һ��ָ�룬ָ�����뷢�͵���Ϣ�ĵ�ַ��
len �����뷢����Ϣ�ĳ��ȡ�
flags ���ͱ�ǡ�һ�㶼��Ϊ0
����:
0-------- �ɹ�
�˳�---ʧ��
�޸�:
��ע:
����
*/
int Send(int sockfd, char *sendbuf, int len, int flags);

int Close(int sockfd);
int Recv(int sockfd, char *recvbuf, int len, int flags);