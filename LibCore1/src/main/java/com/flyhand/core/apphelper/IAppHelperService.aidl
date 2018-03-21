// IErrorReportService.aidl
package com.flyhand.core.apphelper;

// Declare any non-default types here with import statements

interface IAppHelperService {
      boolean reportError(String title,String content);
      boolean networkPingServer(String host);
      boolean pingServerAsync(int timeout);
      boolean canAccessInternet();
      boolean canAccessServer();
      boolean networkAvailable();
}
