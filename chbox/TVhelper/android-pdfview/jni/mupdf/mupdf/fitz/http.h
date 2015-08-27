//http.c
//���ߣ�����
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include "socket.h"

#define FILENAME_LEN 1024
#define URL_LEN  1024
#define PATH_LEN  1024

typedef struct BreakPoint_ST
{
	char url[1024];	
	int recv_size;
	int download_size;
	char filename[1024];
} BreakPoint;



/*
����:�ж϶ϵ���Ч��,����У��url�Ƿ�һ��
����:
����:
>0---------��Ч,�������ļ���С
-1----------��Ч
*/
int Get_Breakpoint_Available(BreakPoint *breakpoint, char *url, char *file_crc);


/*
����:�ж�Ҫ�����ļ��Ƿ���ڶϵ�
����:
filename---Ҫ���ص��ļ���
file_crc----���������������ļ���У����
����:
0---------�޶ϵ�
>0--------�жϵ�,�������ļ���С
*/
int Get_Breakpoint(char *url, char *filename, char *file_crc);

/*
����:����ϵ���Ϣ,�ļ���filename.bp
����:
filename---Ҫ���ص��ļ���
file_crc----���������������ļ���У����
����:
0---------�ɹ�
>0--------�жϵ�,�������ļ���С
*/
int Save_Breakpoint(char *url, char *filename, int download_size, char *file_crc);

/*
����:�����ļ�,׷��д
����:
����:
0---------�ɹ�
*/

int Save_File(char *filebuf, int filelength, char *filename);


int HTTP_GetResponseCode(void);

/*
����:��ȡhttp���ص�Э��ʵ�����峤��
����:
revbuf--------���յ��ķ���ֵ
����ֵ:
>=0---------����(ʵ������)�ĳ���
-1-----------���ݷ��ش���
*/
int HTTP_GetRecvLength(char *revbuf);


/*
����:��ȡhttp���ص�Content-Length����
����:
revbuf--------���յ�������
����ֵ:
>=0---------Content-Length����
-1-----------���ݷ��ش���
*/
int HTTP_GetContentLength(char *revbuf);
int HTTP_GetLengthInContent_Range(char *revbuf);

/*
����:
����:
sockfd--------���յ��ķ���ֵ
����ֵ:
>0---------���յ�����
-1----------ʧ��
=0---------����˶Ͽ�����
ע:�ڲ����ջ���10k
*/

int HTTP_Recv(fz_stream *stm, char *buf_recv,int len,int rec_len);

/*
����:��ȡ����url�е��ļ���,���һ��/����ַ�
����:
����ֵ:
0-----------�ɹ�
-1----------ʧ��
ע:�ڲ����ջ���10k
*/

int HTTP_GetFileName(char *url, char *filename);

/*
����:��ȡ����url�е�·��,��һ��/����ַ�
����:
����ֵ:
0-----------�ɹ�
-1----------ʧ��
ע:url ex "http://host:port/path"
*/
int HTTP_GetPath(char *url, char *path);
/*
����:��ȡ����url�е�ip��port,ip֧������,�˿�Ĭ��Ϊ80
����:
����ֵ:
1-----------����ʽ
2-----------ip portʽ
-1----------ʧ��
ע:url ex "http://host:port/path"
*/

int HTTP_Get_IP_PORT(char *url, char *ip, char *port);
void Package_Url_Get_File(char *host,char *path, char *range);
int Package_Url_Get_FileSize(char *host,char *url);
int HTTP_GetFileSize(fz_stream *stmp,char *path);




/*
����:�ֶ������ļ�
����:
����ֵ:
>0----------�������ļ���С(�������ϴ�����)
-1----------ʧ��
*/
int HTTP_GetFile(int sockfd, char *path, int filelength, int download_size, char *filebuf);

/*
����:HTTP�����ļ�
����:
����ֵ:
0----------�������
-1---------ʧ��
-2---------�����������
ע:�����ļ���bin����Ŀ¼
*/
int HTTP_DownloadFile(char *url, char *save_path);

char HttpIsHttpFile(const char* url);