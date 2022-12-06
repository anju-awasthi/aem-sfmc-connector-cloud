(function($, ns, channel, window, undefined) {

    ns.actions.publishToSfmc = function() {
    	var servletPath = "/conf/aem-sfmc-connector/servlets/emailSync";
    	var pagePath = ns.ContentFrame.getContentPath();
        var payload = Granite.HTTP.internalize(servletPath),
            dialog = document.querySelector('#sfmcDialog');
        dialog.on('coral-overlay:close', function(event) {});

        var promise = $.ajax(payload + '.json', {
            'type': 'GET'
        });
        
        promise.done(function(json) {
            if (JSON.stringify(json.sfmcEmailId) != undefined) {
                $("#sfmcDialog").find(".coral3-Dialog-content").text("This action will update the existing template present in Salesforce Marketing Campaign, Click to Proceed");
            }
        });
        
        document.querySelector('#sfmcDialog').show();
        dialog.on('click', '#sfmc-submitButton', function(e) {
            e.preventDefault();
            $(":submit").attr("disabled", true);
            $.ajax({
                type: 'POST',
                url: payload + '.email.json?pagePath='+pagePath,
                success: function() {
                    $(":submit").removeAttr("disabled");
                    dialog.hide();
                    location.reload();
                },
                error: function() {
                    $(":submit").removeAttr("disabled");
                    document.querySelector('#sfmcDialog').hide();
                    document.querySelector('#sfmcFailedDialog').show();
                }
            });

        });
    };

    channel.on("click", ".pageinfo-publishtosfmc", function() {
        ns.actions.publishToSfmc();
    });

    $(document).on("foundation-contentloaded", function() {

        var dialog = new Coral.Dialog().set({
                id: 'sfmcDialog',
                header: {
                    innerHTML: 'Publish to SFMC'
                },
                content: {
                    innerHTML: 'This action will Sync the template to Salesforce Marketing Campaign, Click to Proceed'
                },
                footer: {
                    innerHTML: '<button id="sfmc-cancelButton" is="coral-button" variant="default" coral-close>Cancel</button><button id="sfmc-submitButton" is="coral-button" variant="primary">Proceed</button>'
                }
            }),
            sfmcFailedDialog = new Coral.Dialog().set({
                id: 'sfmcFailedDialog',
                header: {
                    innerHTML: 'Publish to SFMC'
                },
                content: {
                    innerHTML: ' Template sync to SalesForce Marketing Campaign Failed'
                },
                footer: {
                    innerHTML: '<button id="sfmc-cancelButton" is="coral-button" variant="default" coral-close>Cancel</button>'
                }
            });

        document.body.appendChild(dialog);
        document.body.appendChild(sfmcFailedDialog);

    });

}(jQuery, Granite.author, jQuery(document), this));