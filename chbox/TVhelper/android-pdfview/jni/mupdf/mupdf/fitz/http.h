//http.c
//作者：王振
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
功能:判断断点有效性,现在校验url是否一致
参数:
返回:
>0---------有效,已下载文件大小
-1----------无效
*/
int Get_Breakpoint_Available(BreakPoint *breakpoint, char *url, char *file_crc);


/*
功能:判断要下载文件是否存在断点
参数:
filename---要下载的文件名
file_crc----服务器返回下载文件的校验码
返回:
0---------无断点
>0--------有断点,已下载文件大小
*/
int Get_Breakpoint(char *url, char *filename, char *file_crc);

/*
功能:保存断点信息,文件名filename.bp
参数:
filename---要下载的文件名
file_crc----服务器返回下载文件的校验码
返回:
0---------成功
>0--------有断点,已下载文件大小
*/
int Save_Breakpoint(char *url, char *filename, int download_size, char *file_crc);

/*
功能:保存文件,追加写
参数:
返回:
0---------成功
*/

int Save_File(char *filebuf, int filelength, char *filename);


int HTTP_GetResponseCode(void);

/*
功能:读取http返回的协议实体主体长度
参数:
revbuf--------接收到的返回值
返回值:
>=0---------内容(实体主体)的长度
-1-----------数据返回错误
*/
int HTTP_GetRecvLength(char *revbuf);


/*
功能:读取http返回的Content-Length长度
参数:
revbuf--------接收到的数据
返回值:
>=0---------Content-Length长度
-1-----------数据返回错误
*/
int HTTP_GetContentLength(char *revbuf);
int HTTP_GetLengthInContent_Range(char *revbuf);

/*
功能:
参数:
sockfd--------接收到的返回值
返回值:
>0---------接收到长度
-1----------失败
=0---------服务端断开连接
注:内部接收缓冲10k
*/

int HTTP_Recv(fz_stream *stm, char *buf_recv,int len,int rec_len);

/*
功能:获取下载url中的文件名,最后一个/后的字符
参数:
返回值:
0-----------成功
-1----------失败
注:内部接收缓冲10k
*/

int HTTP_GetFileName(char *url, char *filename);

/*
功能:获取下载url中的路径,第一个/后的字符
参数:
返回值:
0-----------成功
-1----------失败
注:url ex "http://host:port/path"
*/
int HTTP_GetPath(char *url, char *path);
/*
功能:获取下载url中的ip和port,ip支持域名,端口默认为80
参数:
返回值:
1-----------域名式
2-----------ip port式
-1----------失败
注:url ex "http://host:port/path"
*/

int HTTP_Get_IP_PORT(char *url, char *ip, char *port);
void Package_Url_Get_File(char *host,char *path, char *range);
int Package_Url_Get_FileSize(char *host,char *url);
int HTTP_GetFileSize(fz_stream *stmp,char *path);




/*
功能:分段下载文件
参数:
返回值:
>0----------已下载文件大小(不包含上次下载)
-1----------失败
*/
int HTTP_GetFile(int sockfd, char *path, int filelength, int download_size, char *filebuf);

/*
功能:HTTP下载文件
参数:
返回值:
0----------下载完成
-1---------失败
-2---------部分下载完成
注:保存文件到bin所在目录
*/
int HTTP_DownloadFile(char *url, char *save_path);

char HttpIsHttpFile(const char* url);