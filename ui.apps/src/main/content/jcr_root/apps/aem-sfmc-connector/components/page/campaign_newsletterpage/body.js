"use strict";

use(["/libs/wcm/foundation/components/utils/ResourceUtils.js"], function (ResourceUtils) {
    
     return {
         leftCol: granite.resource.resolve(granite.resource.path + "/parLeft").catch(function(){ return null;}),
         rightCol: granite.resource.resolve(granite.resource.path + "/parRight").catch(function(){ return null;}),
         redirect: granite.resource.resolve(granite.resource.path + "/redirect").catch(function(){ return null;}),
    };
});
