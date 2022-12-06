"use strict";

var global = this;

use(["/libs/wcm/foundation/components/utils/ResourceUtils.js"], function (ResourceUtils) {
    
    var CONST = {
        PROP_DESIGN_PATH: "cq:designPath"
    };

    var _getKeywords = function () {
        var keywords = "";
        if (global.currentPage) {
            var tags = global.currentPage.getTags();
            for (var tagIdx=0 ; tagIdx < tags.length ; tagIdx++) {
                keywords += tags[tagIdx].getTitle();
                keywords += (tagIdx <= tags.length - 1) ? "," : "";
            }
        }
        
        return keywords;
    };
    
    var designPathPromise = ResourceUtils.getContainingPage(granite.resource).then(function (pageResource) {
        return ResourceUtils.getInheritedPageProperty(pageResource, CONST.PROP_DESIGN_PATH)
            .then(function (designPath) {
                return designPath;
            });
    });

    var faviconPathPromise = designPathPromise.then(function (designPath) {
        return designPath + "/favicon.ico";
    });

    var titlePromise = ResourceUtils.getContainingPage(granite.resource).then(function (pageResource) {
        return ResourceUtils.getResource(pageResource.path + "/jcr:content")
            .then(function (contentResource) {
                var title = contentResource.properties["jcr:title"];
                if (!title) {
                    title = pageResource.name;
                }
                
                return title;
            });
    });

    var pagePathPromise = ResourceUtils.getContainingPage(granite.resource).then(function (pageResource) {
        return pageResource.path + ".html";
    });
    
    var keywords = _getKeywords();

     if(wcmmode.disabled) {
        request.setAttribute(Packages.com.day.cq.wcm.api.components.ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true); 
    }
    
    return {
        keywords: keywords,
        description: granite.resource.properties["jcr:description"],
        faviconPath: faviconPathPromise,
        designPath: designPathPromise,
        pagePath: pagePathPromise,
        title: titlePromise,
        clientcontextPath: currentStyle.clientcontextPath || "/etc/clientcontext/campaign",
        segmentsPath: currentStyle.segmentPath || "/etc/segmentation",
        wcmmode: global.wcmmode
    };
});
