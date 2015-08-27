//http.c
//���ߣ�����
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include "fitz_base.h"
#include "fitz_stream.h"
#include "http.h"


#define MAX_RECV_SIZE    1440//Ӳ���������Ľ����ֽ���
//char g_host[URL_LEN];


char g_buf_send[4 * 1024];//���������ݴ���
char g_buf_recv[10 * 1024];//���������ݴ���

/*
����:�ж϶ϵ���Ч��,����У��url�Ƿ�һ��
����:
����:
>0---------��Ч,�������ļ���С
-1----------��Ч
*/
int Get_Breakpoint_Available(BreakPoint *breakpoint, char *url, char *file_crc)
{

	//�ж϶ϵ��Ƿ���Ч,���������ļ�У����
	if ((memcmp(breakpoint->url, url, strlen(url)) == 0) && (breakpoint->recv_size == MAX_RECV_SIZE))
		return breakpoint->download_size;
	else
	{
		return -1;
	}

}


/*
����:�ж�Ҫ�����ļ��Ƿ���ڶϵ�
����:
filename---Ҫ���ص��ļ���
file_crc----���������������ļ���У����
����:
0---------�޶ϵ�
>0--------�жϵ�,�������ļ���С
*/
int Get_Breakpoint(char *url, char *filename, char *file_crc)
{
	char filename_bp[64];
	int fd = -1;
	int ret;
	BreakPoint break_point;

	//�ϵ��ļ��� filename+bp
	sprintf(filename_bp, "%s.bp", filename);

	//����Ƿ����filename�ϵ��ļ�
	fd = open(filename_bp, O_RDONLY, S_IRUSR | S_IWUSR);
	if (fd == -1)
	{
#ifdef DEBUG_HTTP
		fz_throw("no exsit %s\n", filename_bp);
#endif
		return 0;
	}

	//���ڶϵ�
	ret = read(fd, &break_point, sizeof(break_point));
	if (ret != sizeof(break_point))
	{
		perror("ERR:Get_Breakpoint read");
		exit(-1);
	}

	close(fd);

	//�ж϶ϵ��Ƿ���Ч
	ret = Get_Breakpoint_Available(&break_point, url, file_crc);
	if (ret > 0)
		return ret;
	else
	{

		fz_throw("%s not available\n", filename_bp);
		remove(filename);
		remove(filename_bp);
		return 0;

	}
}

/*
����:����ϵ���Ϣ,�ļ���filename.bp
����:
filename---Ҫ���ص��ļ���
file_crc----���������������ļ���У����
����:
0---------�ɹ�
>0--------�жϵ�,�������ļ���С
*/
int Save_Breakpoint(char *url, char *filename, int download_size, char *file_crc)
{
	int fd;
	BreakPoint breakpoint;
	char filename_bp[128];//�ϵ���Ϣ�ļ���������·��

	sprintf(filename_bp, "%s.bp", filename);
	/* ����Ŀ���ļ� */
	if ((fd = open(filename_bp, O_WRONLY | O_CREAT, S_IRUSR | S_IWUSR)) == -1)
	{
		fprintf(stderr, "Open %s Error��%s\n", filename_bp, strerror(errno));
		exit(1);
	}
	memset(&breakpoint, 0x0, sizeof(breakpoint));
	strcpy(breakpoint.url, url);
	//strcpy(breakpoint.crc,file_crc);
	strcpy(breakpoint.filename, filename);
	breakpoint.download_size = download_size;
	breakpoint.recv_size = MAX_RECV_SIZE;

	//xu tioa zheng wei fen ci xie ru
	if (write(fd, &breakpoint, sizeof(breakpoint)) != sizeof(breakpoint))
	{
		perror("ERR:Save_Breakpoint");
		exit(1);
	}

	close(fd);

	return 0;



}

/*
����:�����ļ�,׷��д
����:
����:
0---------�ɹ�
*/

int Save_File(char *filebuf, int filelength, char *filename)
{
	int fd;
	/* ����Ŀ���ļ�׷��д */
	if ((fd = open(filename, O_WRONLY | O_CREAT | O_APPEND, S_IRUSR | S_IWUSR)) == -1)
	{
		fprintf(stderr, "Open %s Error��%s\n", filename, strerror(errno));
		exit(1);
	}
	//xu tioa zheng wei fen ci xie ru
	if (write(fd, filebuf, filelength) != filelength)
	{
		perror("ERR:Save_File");
		exit(1);
	}

	close(fd);

	return 0;


}


int HTTP_GetResponseCode(void)
{


}

/*
����:��ȡhttp���ص�Э��ʵ�����峤��
����:
revbuf--------���յ��ķ���ֵ
����ֵ:
>=0---------����(ʵ������)�ĳ���
-1-----------���ݷ��ش���
*/
int HTTP_GetRecvLength(char *revbuf)
{
	char *p1 = NULL;
	int HTTP_Body = 0;//�����峤��
	int HTTP_Head = 0;//HTTP Э��ͷ����


	HTTP_Body = HTTP_GetContentLength(revbuf);
	if (HTTP_Body == -1)
		return -1;

	p1 = strstr(revbuf, "\r\n\r\n");
	if (p1 == NULL)
		return -1;
	else
	{
		HTTP_Head = p1 - revbuf + 4;// 4��\r\n\r\n�ĳ���
		return HTTP_Body + HTTP_Head;
	}


}


/*
����:��ȡhttp���ص�Content-Length����
����:
revbuf--------���յ�������
����ֵ:
>=0---------Content-Length����
-1-----------���ݷ��ش���
*/
int HTTP_GetContentLength(char *revbuf)
{
	char *p1 = NULL, *p2 = NULL;
	int HTTP_Body = 0;//�����峤��

	p1 = strstr(revbuf, "Content-Length");
	if (p1 == NULL)
		return -1;
	else
	{
		p2 = p1 + strlen("Content-Length") + 2;
		HTTP_Body = atoi(p2);
		return HTTP_Body;
	}

}

/*
����:��ȡhttp���ص�Content-Length����
����:
revbuf--------���յ�������
����ֵ:
>=0---------Content-Length����
-1-----------���ݷ��ش���
*/
int HTTP_GetLengthInContent_Range(char *revbuf)
{
	char *p1 = NULL, *p2 = NULL;
	int HTTP_Body = 0;//�����峤��

	p1 = strstr(revbuf, "Content-Range");
	if (p1 == NULL)
	{
		return -1;
	}
	else
	{
		p2 = strchr(p1,'/') + 1;//p1 + strlen("Content-Range") + 2;
		HTTP_Body = atoi(p2);
		return HTTP_Body;
	}

}

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

int HTTP_Recv(fz_stream *stm, char *buf_recv,int len,int rec_len)
{
	int ret;
	int recvlen = 0;
	int downloadlen = 0;
	
	memset(buf_recv, 0x0, len);
	while (1)
	{
		ret = Recv(stm->fsocket.handle, buf_recv + recvlen, rec_len - recvlen, 0);

		if (ret <= 0)//����ʧ��
		{
			perror("ERR:recv fail");
			return ret;
		}


		if (recvlen == 0)
		{
#ifdef DEBUG_HTTP_RECV
			fz_throw("recv len = %d\n", ret);
			fz_throw("recv bp= %s\n", stm->buffer->bp);
			fz_throw("recv = %s\n", buf_recv);
#endif
			//��ȡ��Ҫ���س���;
			downloadlen = rec_len;//HTTP_GetRecvLength(buf_recv);


#ifdef DEBUG_HTTP_RECV
			fz_throw("downloadlen = %d\n", downloadlen);
#endif
		}

		recvlen += ret;
#ifdef DEBUG_HTTP_RECV
		fz_throw("total recvlen = %d\n", recvlen);
#endif

		if (downloadlen == recvlen)//�������
			break;


	}
	//memcpy(buf_recv, buf_recv_tmp, downloadlen);
	return recvlen;

}

/*
����:��ȡ����url�е��ļ���,���һ��/����ַ�
����:
����ֵ:
0-----------�ɹ�
-1----------ʧ��
ע:�ڲ����ջ���10k
*/

int HTTP_GetFileName(char *url, char *filename)
{
	//��ȡurl�����һ��/�������
	int len;
	int i;

	len = strlen(url);
	for (i = len - 1; i>0; i--)
	{
		if (url[i] == '/')
			break;
	}
	if (i == 0)//���ص�ַ����
	{
		fz_throw("url not contain '/'\n");
		return -1;
	}
	else
	{

		strcpy(filename, url + i + 1);
#ifdef DEBUG_HTTP
		fz_throw("filename=%s\n", filename);
#endif
		return 0;
	}
}

/*
����:��ȡ����url�е�·��,��һ��/����ַ�
����:
����ֵ:
0-----------�ɹ�
-1----------ʧ��
ע:url ex "http://host:port/path"
*/
int HTTP_GetPath(char *url, char *path)
{
	char *p;

	p = strstr(url, "http://");
	if (p == NULL)
	{
		p = strchr(url, '/');
		if (p == NULL)
			return -1;
		else
		{
			strcpy(path, p);
			return 0;
		}
	}
	else
	{
		p = strchr(url + strlen("http://"), '/');
		if (p == NULL)
			return -1;
		else
		{
			strcpy(path, p);
			return 0;
		}
	}

}
/*
����:��ȡ����url�е�ip��port,ip֧������,�˿�Ĭ��Ϊ80
����:
����ֵ:
1-----------����ʽ
2-----------ip portʽ
-1----------ʧ��
ע:url ex "http://host:port/path"
*/

int HTTP_Get_IP_PORT(char *url, char *ip, char *port)
{
	char *p = NULL;
	int offset = 0;
	char DOMAIN_NAME[128];

	p = strstr(url, "http://");
	if (p == NULL)
	{
		offset = 0;
	}
	else
	{
		offset = strlen("http://");
	}

	p = strchr(url + offset, '/');
	if (p == NULL)
	{
		fz_throw("url:%s format error\n", url);
		return -1;

	}
	else
	{

		memset(DOMAIN_NAME, 0x0, sizeof(DOMAIN_NAME));
		memcpy(DOMAIN_NAME, url + offset, (p - url - offset));
		p = strchr(DOMAIN_NAME, ':');
		if (p == NULL)
		{
			strcpy(ip, DOMAIN_NAME);
			strcpy(port, "80");
			//fz_throw("ip %p,port %p\n",ip,port);

#ifdef DEBUG_HTTP
			fz_throw("ip=%s,port=%s\n", ip, port);//debug info
#endif
			return 1;

		}
		else
		{
			*p = '\0';

			strcpy(ip, DOMAIN_NAME);
			strcpy(port, p + 1);

#ifdef DEBUG_HTTP
			fz_throw("ip=%s,port=%s\n", ip, port);//debug info
#endif
			return 2;

		}


		return 0;
	}

}
void Package_Url_Get_File(char *host,char *path, char *range)
{
	char buf[64];
	memset(g_buf_send, 0x0, sizeof(g_buf_send));
	sprintf(g_buf_send, "GET %s", path);


	//HTTP/1.1\r\n ǰ����Ҫһ���ո�
	strcat(g_buf_send, " HTTP/1.1\r\n");
	strcat(g_buf_send, "Host: ");
	strcat(g_buf_send, host);
	//strcat(g_buf_send, ":");
	//strcat(g_buf_send, PORT);

	sprintf(buf, "\r\nRange: bytes=%s", range);
	strcat(g_buf_send, buf);
	strcat(g_buf_send, "\r\nKeep-Alive: 200");
	strcat(g_buf_send, "\r\nConnection: Keep-Alive\r\n\r\n");


}

int Package_Url_Get_FileSize(char *host,char *url)
{

	memset(g_buf_send, 0x0, sizeof(g_buf_send));
	sprintf(g_buf_send, "HEAD %s", url);

	//HTTP/1.1\r\n ǰ����Ҫһ���ո�
	strcat(g_buf_send, " HTTP/1.1\r\n");
	strcat(g_buf_send, "Host: ");
	strcat(g_buf_send, host);
	strcat(g_buf_send, "\r\nRange: bytes=0-0");
	//strcat(g_buf_send, ":");
	//strcat(g_buf_send, PORT);
	strcat(g_buf_send, "\r\nConnection: Keep-Alive\r\n\r\n");

	return 0;
}


int HTTP_GetFileSize(fz_stream *stm,char *path)
{
	int ret = -1;
	char buf_recv_tmp[10 * 1024 + 1];

	Package_Url_Get_FileSize(stm->fsocket.host,path);
#ifdef DEBUG_HTTP
	fz_throw("send = %s \n", g_buf_send);
#endif

	Send(stm->fsocket.handle, g_buf_send, strlen(g_buf_send), 0);

	memset(buf_recv_tmp, 0x0, sizeof(buf_recv_tmp));
	ret = Recv(stm->fsocket.handle, buf_recv_tmp, sizeof(buf_recv_tmp)-1, 0);
#ifdef DEBUG_HTTP
	fz_throw("recv len = %d\n", ret);
	fz_throw("recv = %s\n", buf_recv_tmp);
#endif
	if (ret <= 0)
	{
		perror("ERR:recv fail GetFileSize()");
		return -1;

	}
	ret = HTTP_GetLengthInContent_Range(buf_recv_tmp);//HTTP_GetContentLength(buf_recv_tmp);
	Recv(stm->fsocket.handle, buf_recv_tmp, sizeof(buf_recv_tmp)-1, 0);
	if (ret <= 0)
		return -1;
	else
		return ret;


}

/*
����:�ֶ������ļ�
����:
����ֵ:
>0----------�������ļ���С(�������ϴ�����)
-1----------ʧ��
*/
int HTTP_GetContent(fz_stream *stm, char* content_begin, int content_size)
{
	int count;
	int count_once = 0;
	char range[32];
	int i;
	int j = 0;//�ɹ����ش���
	int ret = -1;
	char *p = NULL;
	int download_index;//���ؿ�ʼ����
	int filelength = stm->fsocket.fsize >= (stm->fsocket.pos + content_size) 
		? content_size 
		: stm->fsocket.fsize - stm->fsocket.pos;

	count = (filelength%MAX_RECV_SIZE) ? (filelength / MAX_RECV_SIZE + 1) : (filelength / MAX_RECV_SIZE);

	download_index = 0;//(stm->buffer->wp - stm->buffer->bp)/ MAX_RECV_SIZE;

	for (i = download_index; i<count; i++)
	{
		//if(i == 20)//���Զϵ�
		//break;


		if ((i == (count - 1)) && (content_size%MAX_RECV_SIZE))
		{
			sprintf(range, "%d-%d",stm->fsocket.pos + i*MAX_RECV_SIZE, stm->fsocket.pos + content_size - 1);
			count_once =  content_size - i*MAX_RECV_SIZE;//content_size - i*MAX_RECV_SIZE;
		}
		else
		{
			sprintf(range, "%d-%d",stm->fsocket.pos + i*MAX_RECV_SIZE, stm->fsocket.pos + (i + 1)*MAX_RECV_SIZE - 1);
			count_once = MAX_RECV_SIZE;
		}


		Package_Url_Get_File(stm->fsocket.host,stm->fsocket.path, range);
#ifdef DEBUG_HTTP
		fz_throw("send = %s \n", g_buf_send);
#endif
		Send(stm->fsocket.handle, g_buf_send, strlen(g_buf_send), 0);
		Recv(stm->fsocket.handle, g_buf_recv, sizeof(g_buf_recv)-1, 0);

		/*���Ϊ��ȡhttp ����Э��ͷ��Э�����ܳ�,Ȼ�󶨳�����*/
		memset(g_buf_recv, 0x0, sizeof(g_buf_recv));
		ret = HTTP_Recv(stm, g_buf_recv,sizeof(g_buf_recv),count_once);
		if (ret < 0)
			break;
		if (ret == 0)//����˶Ͽ�����
		{
			stm->fsocket.handle = Socket_Connect(stm->fsocket.ip, stm->fsocket.port);
			i--;
			continue;
		}
		/*��ȡЭ��������,������filebuf��*/
		//p = strstr(g_buf_recv, "\r\n\r\n");
		//if (p == NULL)//jia ru duan dian baocun
		//{
		//	fz_throw("ERR:g_buf_recv not contain end flag\n");
		//	break;
		//}
		//else
		{
			if(i == (count - 1))
			{
				memcpy(content_begin + j*MAX_RECV_SIZE, g_buf_recv, filelength - j*MAX_RECV_SIZE);
			}
			else
			{
				memcpy(content_begin + j*MAX_RECV_SIZE, g_buf_recv, MAX_RECV_SIZE);
			}

			j++;

		}
	}
#ifdef DEBUG_HTTP_RECV
			fz_throw("recv bp= %s\n", content_begin);
#endif			
	if (i == count)
		return (filelength);
	else
		return (i*MAX_RECV_SIZE);
}

/*
����:HTTP�����ļ�
����:
����ֵ:
0----------�������
-1---------ʧ��
-2---------�����������
ע:�����ļ���bin����Ŀ¼
*/
/*int HTTP_DownloadFile(char *url, char *save_path)
{
	int ret;
	int sockfd;
	int filesize;
	int download_size;
	char filename[FILENAME_LEN + 1];
	char filename_bp[FILENAME_LEN + 3 + 1];
	char *filebuf;
	char save_file_path[FILENAME_LEN + 1];//���������ļ���·��+�ļ���

	char path[PATH_LEN + 1];//url�е�path

	//��ȡip��port��url(url �ݲ�ʵ��,��Ҫgethostbyname linux)
	ret = HTTP_Get_IP_PORT(url, g_ip, g_port);
	if (ret == -1)
		return -1;
	else
	{
		sprintf(g_host, "%s:%s", g_ip, g_port);
	}
	//��ȡ�����ļ���
	ret = HTTP_GetFileName(url, filename);
	if (ret == -1)
		return -1;

	ret = HTTP_GetPath(url, path);
	if (ret == -1)
		return -1;
	//sleep(3);//debug info
	//��������
	sockfd = Socket_Connect(g_ip, g_port);

	//��ȡ�����ļ��ܴ�С
	filesize = HTTP_GetFileSize(sockfd, path);
	if (filesize == -1)
		return -1;
	//#ifdef DEBUG_HTTP
	fz_throw("http need download size %d\n", filesize);
	//#endif
	//malloc����洢�ļ��ռ�
	filebuf = (char *)malloc(filesize);
	if (filebuf == NULL)
	{
		perror("malloc filebuf fail");
		return -1;
	}
	else
		memset(filebuf, 0x0, filesize);

	download_size = Get_Breakpoint(url, filename, NULL);
#ifdef DEBUG_HTTP
	fz_throw("breakpoint download_size=%d\n", download_size);//debug info
	sleep(3);//debug info
#endif
	//�ֶ������ļ�
	ret = HTTP_GetFile(sockfd, path, filesize, download_size, filebuf);
	Close(sockfd);
	if (ret < 0)
	{
		free(filebuf);
		return -1;
	}
	else
	{

		sprintf(save_file_path, "%s%s", save_path, filename);

#ifdef DEBUG_HTTP
		fz_throw("save_path=%s\n", save_path);
		fz_throw("filename=%s\n", filename);
		fz_throw("save_file_path=%s\n", save_file_path);
		fz_throw("download_size = %d\n", ret);
#endif
		Save_File(filebuf, ret, save_file_path);
		free(filebuf);
		if ((ret + download_size) == filesize)//ȫ���������
		{
			sprintf(filename_bp, "%s.bp", filename);
			remove(filename_bp);

			fz_throw("download success\n");
			return 0;
		}
		else//�����������
		{
			fz_throw("part download success\n");
			//����ϵ���Ϣ
			Save_Breakpoint(url, save_file_path, ret + download_size, NULL);
			return -2;
		}
	}
}
*/
/*
����:HTTP���ļ�
����:
����ֵ:
0----------�������
-1---------ʧ��
-2---------�����������
ע:�����ļ���bin����Ŀ¼
*/
int HTTP_OpenFile(fz_stream *stm,char *url)
{
	int ret = 0;
	int filesize;
	char filename[FILENAME_LEN + 1];//y

	//��ȡip��port��url(url �ݲ�ʵ��,��Ҫgethostbyname linux)
	while(1)
	{
		ret = HTTP_Get_IP_PORT(url, stm->fsocket.ip, stm->fsocket.port);
		if (ret == -1)
		{
			fz_throw("HTTP_Get_IP_PORT Error:%d",ret);
		    break;
		}
		else
		{
			sprintf(stm->fsocket.host, "%s:%s", stm->fsocket.ip, stm->fsocket.port);
			fz_throw("HTTP_Get_IP_PORT HOST:%s,IP:%s,PORT:%s",stm->fsocket.host,stm->fsocket.ip,stm->fsocket.port);
		}
		//��ȡ�����ļ���
		ret = HTTP_GetFileName(url, filename);
		if (ret == -1)
		{
			fz_throw("HTTP_GetFileName Error:%d FileName:%s",ret,filename);
		    break;
		}

		ret = HTTP_GetPath(url, stm->fsocket.path);
		if (ret == -1)
		{
			fz_throw("HTTP_GetPath Error:%d, Path:%s",ret,stm->fsocket.path);
		    break;
		}
		//sleep(3);//debug info
		//malloc����洢�ļ��ռ�
		//��������
		stm->fsocket.handle= Socket_Connect(stm->fsocket.ip, stm->fsocket.port);

		if(stm->fsocket.handle == -1)
		{			
			ret = -1;
			fz_throw("Socket_Connect Error:%d,",ret);
			break;
		}
		
		//��ȡ�����ļ��ܴ�С
		filesize = HTTP_GetFileSize(stm, stm->fsocket.path);
		if (filesize == -1)
		{			
			ret = -1;
			fz_throw("Socket_Connect Error:%d,FileSize:%d",ret,filesize);
			break;
		}
		stm->fsocket.fsize = filesize;
		stm->fsocket.pos = 0;
		//#ifdef DEBUG_HTTP
		fz_throw("http need download size %d\n", filesize);		
		//#endif
		break;
	}

	if(ret == -1)
	{
    	if(stm != NULL)
		{
			if(stm->fsocket.handle != NULL)
			{
			    Close(stm->fsocket.handle);
				stm->fsocket.handle = NULL;
			}    		   		
    	}		
		return ret;
	}		
	return ret;
}

char HttpIsHttpFile(const char* url)
{
	if(url != NULL)
	{
		char *p = strstr(url, "http://");
		if(p != NULL)
			return 1;
	}
	return 0;
}