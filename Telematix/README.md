"Telematix" application is a POC throw away app written with the sole purpose of adding weather variables of interest to a known csv of latlons & times, the csv is included in the repo
- the csv input file exists at ../src/main/resources/sample_trip.csv
- the app reads through each record in the csv (the app refers to one record as a "moment") and initiates point requests against netcdf files
- the app requires about 350gb of netcdf files to run, for obvious reasons these are not included in the repo (ask Wes Edge for the netcdf files if you need them)
- the app outputs a csv file, you may specify the output path by modifying the "csvPath" variable in ..src/main/resources/config.properties
- config file is a good place to look, you will most certainly need to modify this file if you're gonna run this app, config exists at ../src/main/resources/config.properties
