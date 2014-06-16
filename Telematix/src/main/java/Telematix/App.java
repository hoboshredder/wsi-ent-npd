package Telematix;

import java.io.IOException;
import java.util.List;
import ucar.unidata.geoloc.LatLonPoint;
import org.joda.time.DateTime;
import java.util.Properties;
import java.text.DecimalFormat;
import java.io.InputStream;

public class App 
{

    public static void main( String[] args )
    {

        try {

            // these are the netcdf variables we need
            List<Variable> variables = Variable.getVariables();

            // load the records from the sample trip into a list, sorted by date
            List<Moment> moments = Moment.getMoments(App.getProperties().getProperty("inputPath").toString());

            int counter = 0;

            for (Moment moment : moments){

                String percentComplete = (new DecimalFormat("#.##")).format((counter++ / (double) moments.size()) * 100);

                write(String.format("%s%% complete - begin processing moment.. %s", percentComplete, moment.toString()));

                DateTime utc = moment.getDatetimeUTC();

                // for each netcdf variable
                for (Variable variable : variables){

                    try{

                        LatLonPoint latlon = moment.getLatLon();

                        // is this value already saved? (to avoid netcdf processing time again later)
                        MomentValue momentValue = MomentValue.fromProperties(variable.getName(), moment.getDatetimeUTC());

                        if (null != momentValue){
                            moment.addMomentValue(momentValue);
                            continue;
                        }

                        // get the netcdf file from the system (if it exists)
                        NetCDF.Wrapper netcdf = variable.getNetCDF(utc);

                        // a visual test of the netcdf grid
                        //PNG.netCDFtoPNG(netcdf, "/Users/wedge/NetCDF/netcdf.png");

                        // make a point request
                        float val = netcdf.getGridValue(latlon);

                        // update the moment with the new value
                        momentValue = new MomentValue();
                        momentValue.setVarName(variable.getName());
                        momentValue.setFileTimestamp(variable.getFileTimestamp(netcdf.getFile()));
                        momentValue.setValue(val);
                        momentValue.setUnitOfMeasure(netcdf.getUnitOfMeasure());
                        momentValue.setUTC(utc);

                        moment.addMomentValue(momentValue);

                        // save this value (to avoid netcdf processing time again later)
                        momentValue.toProperties();

                    }catch (Exception e) {
                        //e.printStackTrace();
                        App.write(e.getMessage());
                    }

                }

            }

            new CSV(moments);   // write to csv file
            writeMoments(moments);  // write to console

        }catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void write(String msg){
        System.out.println(msg);
    }

    public static void writeMoments(List<Moment> moments){
        // writes moments to console

        for(Object moment: moments){
            App.write(moment.toString());
        }

    }

    public static Properties getProperties() throws IOException{
        String resourceName = "config.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        InputStream resourceStream = loader.getResourceAsStream(resourceName);
        props.load(resourceStream);
        return props;
    }

}