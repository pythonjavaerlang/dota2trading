    <div id="header">
    [#if isAuthenticated]
        <div class="header_links">
            <div class="header_links_container">
                <span><a href="${rootPath}users/signout" id="logout_link">Logout</a></span>
                <span><label class="header-links-username">Logged in as <a href="${rootPath}player">${username}</a></label></span>
                <div class="clear"></div>
            </div>
        </div>
    [/#if]
        <div id="header_content">
            <div class="header_content_container">
                <div class="header_logo">
                    <h1>&nbsp;&nbsp;<div class="header-module-label" id="page_title" ><img src="${media_url}dota2logo.png" style="position:absolute;"/><span style="padding-left:60px;">Dota2 ragfair</span><span id="page_title"></span></div></h1>
[#if !isAuthenticated ]<a href="j_spring_openid_security_check?openid_identifier=${openid_provider}" title="Login with Steam" class="steam_openid_login"><img class="stiker" src="${media_url}sits_large_border.png" /></a>[/#if]
                </div>
            </div>
        </div>
    </div>
