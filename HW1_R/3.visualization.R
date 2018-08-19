rm(list=ls())
setwd("~/HW1/data/")

#d
jpeg(file="../img/apptype_traffic.jpeg")
apptype_traffic <- read.table("apptype_traffic.txt",sep="\t",header=F,stringsAsFactors=F)
x <-  apptype_traffic[,2]
xp <- c(x[1:4],sum(x[5:17]))
apptype = c('Instant Msg','Reading','Weibo','Nagivate','Video','Music'
            ,'Appstore','Game','Payment','Cartoon','Email','P2P','VolP'
            ,'Text','Browser','Ecnomics','Safety','Others')
names(xp) <- apptype[c(apptype_traffic[1:4,1],18)]
pie(xp, labels = names(xp), edges = 200, radius = 0.8, 
    clockwise = TRUE,col = c("pink", "coral", "darkolivegreen1", "brown", "cadetblue1"),
    density = NULL, angle = 45, border = NULL,  lty = NULL, main = ' Popular Apptype Ordered by Traffic')
dev.off()

#e
jpeg(file="../img/apptype_time.jpeg")
apptype_time <- read.table("apptype_time.txt",sep="\t",header=F,stringsAsFactors=F)
x <-  apptype_time[,2]
xp <- c(x[1:4],sum(x[5:17]))
names(xp) <- apptype[c(apptype_time[1:4,1],18)]
pie(xp, labels = names(xp), edges = 200, radius = 0.8, 
    clockwise = TRUE,col = c("brown", "coral", "darkolivegreen1", "pink", "cadetblue1"),
    density = NULL, angle = 45, border = NULL,  lty = NULL, main = 'Popular Apptype Ordered by Time')
dev.off()


#f
jpeg(file="../img/IMsub.jpeg")
IMsub_traffic <- read.table("IMsub_traffic.txt",sep="\t",header=F,stringsAsFactors=F)
x1 <- IMsub_traffic[,1]
y1 <- IMsub_traffic[,2]
y1 <- y1/sum(y1)
IMsub_time <- read.table("IMsub_time.txt",sep="\t",header=F,stringsAsFactors=F)
x2 <- IMsub_time[,1]
y2 <- IMsub_time[,2]
y2 <- y2/sum(y2)

plot(c(1:10),y1,type = 'o',pch=16,xlab="app sub_type",ylab="popularity in percentage",main="Instant Msg SubType",
     xlim=c(0,10),ylim = c(0,1),col=2 ,xaxt='n') 
axis(1,labels=c("QQ","Wechat","Others"),at=c(0.9,2.1,8),las=1.5,cex.axis=0.7)
lines(c(1:10),y2,type = 'o',pch=16,xlab="app sub_type",ylab="popularity in percentage",main="Instant Msg SubType",
     xlim=c(1,10),ylim = c(0,1),col=3)
legend(7, 1, c('IMsub_traffic','IMsub_time'), col = c(2,3),text.col = c(2,3),cex = 0.9, lty = 1)
dev.off()