# Set nginx base image
FROM nginx

# File Author / Maintainer
MAINTAINER Travis Cummings

# Copy custom configuration file from the current directory
COPY nginx.conf /etc/nginx
RUN mkdir /etc/nginx/sites-available
RUN chmod 777 /etc/nginx/sites-available
RUN mkdir /etc/nginx/sites-enabled

# api-dstu2
COPY api-dstu2.conf /etc/nginx/sites-available
RUN ln -sf /etc/nginx/sites-available/api-dstu2.conf /etc/nginx/sites-enabled/api-dstu2.conf
EXPOSE 8075

# api-dstu2-debug
COPY api-dstu2-debug.conf /etc/nginx/sites-available
RUN ln -sf /etc/nginx/sites-available/api-dstu2-debug.conf /etc/nginx/sites-enabled/api-dstu2-debug.conf
EXPOSE 5075

# api-stu3
COPY api-stu3.conf /etc/nginx/sites-available
RUN ln -sf /etc/nginx/sites-available/api-stu3.conf /etc/nginx/sites-enabled/api-stu3.conf
EXPOSE 8076

# api-stu3-debug
COPY api-stu3-debug.conf /etc/nginx/sites-available
RUN ln -sf /etc/nginx/sites-available/api-stu3-debug.conf /etc/nginx/sites-enabled/api-stu3-debug.conf
EXPOSE 5076

# api-r4
COPY api-r4.conf /etc/nginx/sites-available
RUN ln -sf /etc/nginx/sites-available/api-r4.conf /etc/nginx/sites-enabled/api-r4.conf
EXPOSE 8077

# api-r4-debug
COPY api-r4-debug.conf /etc/nginx/sites-available
RUN ln -sf /etc/nginx/sites-available/api-r4-debug.conf /etc/nginx/sites-enabled/api-r4-debug.conf
EXPOSE 5077
