#data wragling script
#extract specific columns from original data and write result to a new csv file

rm(list=ls())
setwd("~/HW1/data/")
orig_data<- read.csv("103_20150615143630_00_00_000.csv",sep="|",header=F,stringsAsFactors=F)
dim(orig_data)
data <- orig_data[c(17,27,29,59,23,24,20,21,34,35)]
header <- c('region','user_ip','user_port','domain_name','app_type','app_sub_type',
            'start_time','end_time','ul_data','dl_data')
colnames(data) <- header

write.csv(data,file="~/HW1/data/clean_data.csv",quote=F,row.names = F)