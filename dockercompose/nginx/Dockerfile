FROM  nginx 

RUN mkdir /etc/nginx/ssl/

ADD ./etc/nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf
ADD run.sh /run.sh
RUN chmod +x /run.sh
ENTRYPOINT ["/run.sh"]


