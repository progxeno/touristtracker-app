package htwg.trackingapp;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by miosko on 19.11.14.
 */
@ReportsCrashes(formKey = "", // will not be used
        formUri = "http://touristtracking.in.htwg-konstanz.de/reportcrash")
        //formUri = "http://htwg-tourist-tracker.herokuapp.com/reportcrash")
//formUriBasicAuthLogin = "yourlogin", // optional
//formUriBasicAuthPassword = "y0uRpa$$w0rd", // optional)
public class CrashReport extends Application {

    private static ReportsCrashes mReportsCrashes;

    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        mReportsCrashes = this.getClass().getAnnotation(ReportsCrashes.class);
        LocalFileSystemErrorSender jsonSender = new LocalFileSystemErrorSender(mReportsCrashes.formUri());
        ErrorReporter.getInstance().setReportSender(jsonSender);

        super.onCreate();
    }
}
