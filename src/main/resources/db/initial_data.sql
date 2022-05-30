INSERT INTO "itemCapability"(id, name, description) 
	VALUES (nextval('item_capability_id_seq'), 'can_craft_mark', 'The item can have the name attached to it.');
INSERT INTO "itemCapability"(id, name, description) 
	VALUES (nextval('item_capability_id_seq'), 'can_gift_wrap', 'The item can be gift wrapped.');
INSERT INTO "itemCapability"(id, name, description) 
	VALUES (nextval('item_capability_id_seq'), 'paintable_unusual', 'The item can be painted.');
INSERT INTO "itemCapability"(id, name, description) 
	VALUES (nextval('item_capability_id_seq'), 'decodable', 'The item can be opened with a key.');
INSERT INTO "itemCapability"(id, name, description) 
	VALUES (nextval('item_capability_id_seq'), 'usable', 'The item is an Action item.');
INSERT INTO "itemCapability"(id, name, description) 
	VALUES (nextval('item_capability_id_seq'), 'usable_out_of_game', 'The item can be activated while the user is not in-game');
INSERT INTO "itemCapability"(id, name, description) 
	VALUES (nextval('item_capability_id_seq'), 'usable_gc', 'The item can be used from within the backpack.');
INSERT INTO "itemCapability"(id, name, description) 
	VALUES (nextval('item_capability_id_seq'), 'can_be_restored', 'The item can be restored.');
