<ul class="nav">
	<li>
	    <a href="#">Trades</a>
	</li>
	[#if isAuthenticated ]<li>
	    <a href="${rootPath}profile">Profile</a>
	</li>[/#if]
	<li>
	    <a href="#">My Offers</a>
	</li>
	<li>
	    <a href="#">Steam Group</a>
	</li>
	<li>
	    <a href="#">Rules</a>
	</li>
	<li id="search">
	    <form action="" method="get">
		<input type="text" name="search_text" id="search_text"/>
		<input type="button" name="search_button" id="search_button"></a>
	    </form>
	</li>
</ul>
