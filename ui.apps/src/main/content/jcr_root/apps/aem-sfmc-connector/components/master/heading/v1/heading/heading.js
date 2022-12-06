
/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2014 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */

"use strict";

/**
 * Title foundation component JS backing script
 */
use(function () {
    
    var CONST = {
        PROP_TITLE: "jcr:title",
        PROP_PAGE_TITLE: "pageTitle",
        PROP_TYPE: "type",
        PROP_DEFAULT_TYPE: "defaultType",
        PROP_RICH_FORMAT: "textIsRich",
        CONTEXT_TEXT: "text",
        CONTEXT_HTML: "html",
        CONTEXT_ATTRIBUTE: "attribute",
        CONTEXT_STYLE_TOKEN: "styleToken"
    }

    var heading = {};
    
    // The actual title content
     heading.text = granite.resource.properties[CONST.PROP_TITLE]
            || wcm.currentPage.properties[CONST.PROP_PAGE_TITLE]
            || wcm.currentPage.properties[CONST.PROP_TITLE]
            || wcm.currentPage.name ;

    // Wether the text contains HTML or not
    heading.context = granite.resource.properties[CONST.PROP_RICH_FORMAT]
            ? CONST.CONTEXT_HTML : CONST.CONTEXT_TEXT

    // The HTML element name
    heading.element = granite.resource.properties[CONST.PROP_TYPE]
            || currentStyle.get(CONST.PROP_DEFAULT_TYPE, "");

     // Set up placeholder if empty
    if (!heading.text) {
        heading.cssClass = AuthoringUtils.isTouch
                ? "cq-placeholder"
                : "cq-text-placeholder-ipe";
        
        // only dysplay placeholder in edit mode
        if (typeof wcmmode != "undefined" && wcmmode.isEdit()) {
            heading.text = AuthoringUtils.isTouch
            ? ""
            : "Edit text";
        } else {
            heading.text = "";
        }
    }else{
        heading.font = {};
        if(heading.element == "h1") {
            heading.font.size = "+4";
            heading.font.color = "#666666";
            heading.style = "text-transform: uppercase; color: #666666 !important;";
        } else if(heading.element == "h2") {
            heading.font.size = "+3";
            heading.font.color = "#535353";
            heading.style = "text-transform: uppercase; font-weight: bold; border-bottom:1px dotted #999999; margin-top:12px; padding:3px 0; color: #535353 !important;";
        } else if(heading.element == "h3") {
            heading.font.size = "+2";
            heading.font.color = "#4F960F";
            heading.style = "text-transform: uppercase; font-weight: bold; border-bottom:1px dotted #999999; margin-top:12px; padding:2px 0; color: #4F960F !important;";
        } else { 
            heading.font.size = "+1";
            heading.font.color = "#535353";
            heading.style = "font-weight: bold; margin-top:12px; color: #535353 !important;";
        }
    }

    // Adding the constants to the exposed API
    heading.CONST = CONST;
    
    return heading;
    
});

