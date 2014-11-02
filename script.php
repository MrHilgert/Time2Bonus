<?php
mysql_connect("localhost", "root", "");
mysql_select_db("minecraft");

error_reporting(E_ALL);

print_r(getBonus("MrHilgert"));

function getBonus($player){
	return mysql_fetch_array(mysql_query("SELECT * FROM `t2b` WHERE `player`='".mysql_real_escape_string($player)."'"))['bonus'];
}

?>