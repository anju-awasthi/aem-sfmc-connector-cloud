CQ.EmailService.doConnect = function(dialog) {

    var that = this;
    var clientId = dialog.find("name","./clientId")[0];
    var clientSecret = dialog.find("name","./clientSecret")[0];
    var accountId = dialog.find("name","./accountId")[0];
    var authUrl = dialog.find("name","./authUrl")[0];
    var grant = dialog.find("name","./grantType")[0];

    this.showButtonIndicator(dialog, true);

    function fieldEmpty(field, msg) {
        if (!field || field.getValue() == "") {
            that.showButtonIndicator(dialog, false);
            CQ.Ext.Msg.alert(CQ.I18n.getMessage("Error"), msg);
            return true;
        }
        return false;
    }

    if (fieldEmpty(clientId, CQ.I18n.getMessage("Please enter the Client Id.")) ||
        fieldEmpty(clientSecret, CQ.I18n.getMessage("Please enter the Client Secret.")) ||
        fieldEmpty(accountId, CQ.I18n.getMessage("Please enter the Account ID.")) ||
        fieldEmpty(authUrl, CQ.I18n.getMessage("Please enter the SFMC Auth token URL."))) {
        return;
    }

    that.showButtonIndicator(dialog, false);

    var id = clientId.getValue();
    var secret = clientSecret.getValue();
    var account = accountId.getValue();
    var authurl = authUrl.getValue();
    var grant = grant.getValue();
    var path = window.location.pathname;
    path = path.replace('.html','');

    var url = path+"/jcr:content.connect.json";
    url = CQ.utils.HTTP.addParameter(url, "authurl", authurl);
    url = CQ.utils.HTTP.addParameter(url, "grant_type", grant);
    url = CQ.utils.HTTP.addParameter(url, "client_id", id);
    url = CQ.utils.HTTP.addParameter(url, "client_secret", secret);
    url = CQ.utils.HTTP.addParameter(url, "account_id", account);

    response = CQ.utils.HTTP.get(url);

    if (CQ.HTTP.isOk(response)) {
            CQ.cloudservices.getEditOk().enable();
            CQ.Ext.Msg.show({
                "title": CQ.I18n.getMessage("Success"),
                "msg": CQ.I18n.getMessage("Connection tested successfully."),
                "buttons": CQ.Ext.Msg.OK,
                "icon": CQ.Ext.Msg.INFO
            });
    } else {
        CQ.Ext.Msg.alert(CQ.I18n.getMessage("Error"), CQ.I18n.getMessage("Could not connect. Please verify the inputs."));
    }

};