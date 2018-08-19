create database if not exists sy2160 ;
use sy2160;

#import data
drop table if exists telecom;
create table telecom(  region int,  user_ip varchar(30), user_port int,  domain_name varchar(30),  app_type int,  app_sub_type int,
             start_time bigint , end_time bigint, ul_data int, dl_data int);
load data local infile "~/HW1/data/clean_data.csv" 
into table telecom fields terminated by ',' enclosed by '"' lines terminated by '\n' ignore 1 rows;

#1.region vs user
select region, count(distinct user_ip)
from telecom
where region != 0
group by region
order by count(distinct user_port) desc
 into  outfile   "/Users/Lovely-white/HW1/data/region_user.txt";
 
#2. region vs traffic
select region, sum(dl_data + ul_data)
from telecom
where region != 0
group by region
order by sum(dl_data + ul_data)desc
 into  outfile   "/Users/Lovely-white/HW1/data/region_traffic.txt";
 
 #3. region vs time
select region, sum(end_time - start_time)
from telecom
where region != 0
group by region
order by  sum(end_time - start_time)desc
 into  outfile   "/Users/Lovely-white/HW1/data/region_time.txt";


#4.apptype vs traffic
select app_type, sum(dl_data + ul_data)
from telecom
group by app_type
order by sum(dl_data + ul_data) desc
 into  outfile   "/Users/Lovely-white/HW1/data/apptype_traffic.txt";
 
#5.apptype vs time
select app_type, sum(end_time - start_time)
from telecom
group by app_type
order by sum(end_time - start_time) desc
into  outfile   "/Users/Lovely-white/HW1/data/apptype_time.txt";
 


 #6.apptype=1，subtype vs traffic
select app_sub_type, sum(dl_data + ul_data)
from telecom
where app_type = 1
group by app_sub_type
order by sum(dl_data + ul_data) desc
 into  outfile   "/Users/Lovely-white/HW1/data/IMsub_traffic.txt";
 
 #7.apptype=1，subtype vs time
select app_sub_type, sum(end_time - start_time)
from telecom
where app_type = 1
group by app_sub_type
order by sum(end_time - start_time) desc
 into  outfile   "/Users/Lovely-white/HW1/data/IMsub_time.txt";

 
 #8.domain vs traffic
select domain_name, sum(dl_data + ul_data)
from telecom
group by domain_name
order by sum(dl_data + ul_data) desc
 into  outfile   "/Users/Lovely-white/HW1/data/domain_traffic.txt";
 
 #9.omain vs time
select domain_name, sum(end_time - start_time)
from telecom
group by domain_name
order by sum(end_time - start_time) desc
 into  outfile   "/Users/Lovely-white/HW1/data/domain_time.txt";
 

