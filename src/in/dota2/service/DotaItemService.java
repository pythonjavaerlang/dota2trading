package in.dota2.service;

import in.dota2.model.DotaAttribute;
import in.dota2.model.DotaImageCatalogDAOInterface;
import in.dota2.model.DotaItem;
import in.dota2.model.DotaItemAttribute;
import in.dota2.model.DotaItemCapability;
import in.dota2.model.DotaItemCatalogDAOInterface;
import in.dota2.model.DotaItemQuality;
import in.dota2.model.DotaPlayer;
import in.dota2.model.DotaPlayerCatalogDAOInterface;
import in.dota2.model.DotaPlayerItem;
import in.dota2.model.DotaPlayerItemAttribute;
import in.dota2.model.DotaImage;
import in.dota2.util.ImageResponse;
import in.dota2.util.SpringPropertiesUtil;
import in.dota2.util.SteamHttpRequest;
import in.dota2.util.SteamResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

public class DotaItemService {

	@Autowired
	private DotaItemCatalogDAOInterface dotaItemCatalogDAO;

	@Autowired
	private DotaPlayerCatalogDAOInterface dotaPlayerCatalogDAO;

	@Autowired
	private DotaImageCatalogDAOInterface dotaImageCatalogDAO;

	protected int steamAPIVersion = 1;

	private static final Logger logger = Logger
			.getLogger(DotaPlayerSummaryService.class);

	@Async
	public void retrieveAndStoreItems() {
		/*
		 * Tries to retrieve all game items from Steam. On success save them to
		 * DB.
		 */
		if (logger.isDebugEnabled()) {
			logger.debug("retrieveAndStoreItems called");
		}

		String key = SpringPropertiesUtil.getProperty("global.steam_api_key");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", key);
		params.put("language", "en");
		params.put("format", "json");

		String url = String.format("http://api.steampowered.com/IEconItems_570/GetSchema/v%04d/?", steamAPIVersion);
		SteamResponse response = null;
		try {
			response = SteamHttpRequest.sendHttpRequest(url, params);
		} catch (Exception e) {
			logger.error(e);
		}

		if (logger.isDebugEnabled() && response != null) {
			logger.debug(String.format("Response code: %d",
					response.getResponseCode()));
		}

		if (response.getResponseCode() != 200) {
			return;
		}

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode rootNode = objectMapper.readTree(response.response);
			JsonNode result = rootNode.get("result");

			JsonNode statusNode = result.get("status");
			String status = statusNode.asText();
			if (!status.equals("1")) {
				logger.info(String.format("retrieveAndStoreItems: Steam server returned status %s. Stopping.", status));
				return;
			};

			JsonNode qualityNamesNode = result.get("qualityNames");
			Map<String, String> qualityNames = new HashMap<String, String>();
			Iterator<Map.Entry<String, JsonNode>> qualityNameRecords = qualityNamesNode.getFields();
			while (qualityNameRecords.hasNext()) {
				Map.Entry<String, JsonNode> entry = qualityNameRecords.next();
				String qualityKey = entry.getKey();
				String qualityValue = entry.getValue().asText();
				qualityNames.put(qualityKey, qualityValue);
			}

			JsonNode qualitiesNode = result.get("qualities");
			Map<Integer, String> qualities = new HashMap<Integer, String>();
			Iterator<Map.Entry<String, JsonNode>> qualityRecords = qualitiesNode.getFields();
			while (qualityRecords.hasNext()) {
				Map.Entry<String, JsonNode> entry = qualityRecords.next();
				String qualityKey = entry.getKey();
				Integer qualityValue = Integer.parseInt(entry.getValue().asText());
				qualities.put(qualityValue, qualityKey);

				if(dotaItemCatalogDAO.getItemQuality(qualityValue)==null){
					DotaItemQuality itemQuality = new DotaItemQuality();
					itemQuality.setKey(qualityValue);
					itemQuality.setValue(qualityKey);
					itemQuality.setName(qualityNames.get(qualityValue));
					dotaItemCatalogDAO.saveItemQuality(itemQuality);
				};
			}

			JsonNode originNamesNode = result.get("originNames");
			Map<Integer, String> origins = new HashMap<Integer, String>();
			Iterator<JsonNode> originNodes = originNamesNode.getElements();
			while (originNodes.hasNext()) {
				JsonNode originNode = originNodes.next();
				Integer originKey = Integer.parseInt(originNode.get("origin").asText());
				String originValue = originNode.get("name").asText();
				origins.put(originKey, originValue);
			}

			JsonNode attributesNode = result.get("attributes");
			Map<String, Map<String, String>> attributes = new HashMap<String, Map<String, String>>();
			if (attributesNode != null) {
				Iterator<JsonNode> attributeRecords = attributesNode.getElements();
				while (attributeRecords.hasNext()) {
					JsonNode attribute = attributeRecords.next();
					String attributeName = attribute.get("name").asText();
					Integer uniqueIndex = attribute.get("defindex").asInt();
					JsonNode effectType = attribute.get("effect_type");
					JsonNode hidden = attribute.get("hidden");
					JsonNode descriptionFormat = attribute.get("description_format");
					JsonNode descriptionString = attribute.get("description_string");

					Map<String, String> value = new HashMap<String, String>();
					if (effectType != null)
						value.put("effectTypeDisplay", effectType.asText());
					if (hidden != null)
						value.put("hidden", hidden.asText());
					if (descriptionFormat != null)
						value.put("descriptionFormat", descriptionFormat.asText());
					if (descriptionString != null)
						value.put("descriptionString", descriptionString.asText());

					DotaAttribute dotaAttribute = dotaItemCatalogDAO.getAttributeByUniqueIndex(uniqueIndex);
					if(dotaAttribute==null){
						dotaAttribute = new DotaAttribute();
						dotaAttribute.setUniqueIndex(uniqueIndex);
					}
					dotaAttribute.setName(attributeName);
					if(descriptionFormat!=null) dotaAttribute.setDescriptionFormat(descriptionFormat.asText());
					if(descriptionString!=null) dotaAttribute.setDescriptionString(descriptionString.asText());
					dotaAttribute.setHidden(hidden.asBoolean());
					dotaItemCatalogDAO.saveAttribute(dotaAttribute);
					attributes.put(attributeName, value);
				}
			}

			JsonNode itemsNode = result.get("items");
			Iterator<JsonNode> items = itemsNode.getElements();

			if (items == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("No items arrived in response.");
				}
				return; // no point to continue
			}

			while (items.hasNext()) {
				JsonNode item = items.next();
				Integer uniqueIndex = item.get("defindex").asInt();

				DotaItem dotaItem = dotaItemCatalogDAO.getItemByUniqueIndex(uniqueIndex);
				if (dotaItem == null) {
					dotaItem = new DotaItem();
					dotaItem.setUniqueIndex(uniqueIndex);
				}
				JsonNode value = item.get("name");
				if (value != null)
					dotaItem.setName(value.asText());

				value = item.get("item_type_name");
				if (value != null)
					dotaItem.setTypeDisplay(value.asText());

				JsonNode description = item.get("item_description");
				if (description != null)
					dotaItem.setDescription(description.asText());

				JsonNode properName = item.get("proper_name");
				if (properName != null)
					dotaItem.setProperName(properName.asBoolean());

				value = item.get("item_quality");
				if (value != null) {
					Integer quality = Integer.parseInt(value.asText());
					dotaItem.setQuality(quality);
					String qualityDisplay = qualities.get(quality);
					dotaItem.setQualityDisplay(qualityDisplay);
				}

				value = item.get("image_url");
				if (value != null){
					DotaImage image = dotaItem.getImage();
					Integer contentLength = null;
					if(image==null){
						image = new DotaImage();
					} else {
						contentLength = image.getFileSize();
					}

					String imageUrl = value.asText();
					if(imageUrl.equals("")){
						dotaItem.setImage(null);
					} else {
						ImageResponse imageResponse = null;
						try {
							imageResponse = SteamHttpRequest.downloadImage(imageUrl, contentLength);
							if(imageResponse!=null){
								dotaImageCatalogDAO.saveImage(image);
								image.write(imageResponse); // the pk required
								dotaImageCatalogDAO.saveImage(image);
								dotaItem.setImage(image);
							}
						} catch (Exception e) {
							logger.error(e);
						}
					}
				}

				value = item.get("image_url_large");
				if (value != null){
					DotaImage image = dotaItem.getImage();

					if(image!=null){
						String imageUrl = value.asText();
						if(!imageUrl.equals("")){
							DotaImage largerImage = image.getImage();
							Integer contentLength = null;
							if(largerImage==null){
								largerImage = new DotaImage();
							} else {
								contentLength = largerImage.getFileSize();
							}

							ImageResponse imageResponse = null;
							try {
								imageResponse = SteamHttpRequest.downloadImage(imageUrl, contentLength);
								if(imageResponse!=null){
									dotaImageCatalogDAO.saveImage(largerImage);
									largerImage.write(imageResponse); // the pk required
									image.setImage(largerImage);
									dotaImageCatalogDAO.saveImage(largerImage);
								}
							} catch (Exception e) {
								logger.error(e);
							}
						}
					}
				}

				/*
				value = item.get("image_url_large");
				if (value != null)
					dotaItem.setImageUrlLarge(value.asText());
				*/
				List<DotaItemCapability> capabilities = new ArrayList<DotaItemCapability>();
				JsonNode capabilitiesNode = item.get("capabilities");
				if (capabilitiesNode != null) {
					Iterator<Map.Entry<String, JsonNode>> capabilityRecords = capabilitiesNode
							.getFields();
					while (capabilityRecords.hasNext()) {
						Map.Entry<String, JsonNode> entry = capabilityRecords
								.next();
						String capabilityKey = entry.getKey();
						Boolean capabilityValue = entry.getValue().asBoolean();

						if (capabilityValue == true) {
							DotaItemCapability capability = new DotaItemCapability();
							capability.setName(capabilityKey);
							String[] bits = capabilityKey.split("_");

							StringBuilder sb = new StringBuilder();
							for (int i = 0; i != bits.length; i++) {
								sb.append(String.format("%s%s", bits[i]
										.substring(0, 1).toUpperCase(), bits[i]
										.substring(1)));
								if (i != bits.length - 1)
									sb.append(" ");
							}
							capability.setDescription(sb.toString());
							capabilities.add(capability);
						}
					}
				}

				for (int i = 0; i != capabilities.size(); i++) {
					DotaItemCapability capability = capabilities.get(i);
					if (!dotaItem.hasCapability(capability.getName())) {
						dotaItemCatalogDAO.saveItemCapability(capability);
						dotaItem.addCapability(capability);
					}
					;
				}

				dotaItemCatalogDAO.saveItem(dotaItem);

				JsonNode itemAttributesNode = item.get("attributes");
				if (itemAttributesNode != null) {
					Iterator<JsonNode> itemAttributeRecords = itemAttributesNode.getElements();
					while (itemAttributeRecords.hasNext()) {
						JsonNode entry = itemAttributeRecords.next();

						String attributeName = entry.get("name").asText();
						String attributeValue = entry.get("value").asText();
						DotaAttribute dotaAttribute = dotaItemCatalogDAO.getAttributeByName(attributeName);
						if(dotaAttribute==null) continue;
						Integer attributeUniqueIndex = dotaAttribute.getUniqueIndex(); 

						DotaItemAttribute dotaItemAttribute;
						if (dotaItem.hasAttribute(attributeUniqueIndex)) {
							dotaItemAttribute = dotaItem.getAttribute(attributeUniqueIndex);
						} else {
							dotaItemAttribute = new DotaItemAttribute();
						}

						dotaItemAttribute.setName(attributeName);
						dotaItemAttribute.setValue(attributeValue);

						Map<String, String> cachedAttribute = attributes.get(attributeName);
						if (cachedAttribute != null) {

							String effectTypeDisplay = cachedAttribute.get("effectTypeDisplay");
							if (effectTypeDisplay != null)
								dotaItemAttribute.setEffectTypeDisplay(effectTypeDisplay);

							String hidden = cachedAttribute.get("hidden");
							if (hidden != null)
								dotaItemAttribute.setHidden(Boolean.parseBoolean(hidden));

							String descriptionFormat = cachedAttribute.get("descriptionFormat");
							if (descriptionFormat != null)
								dotaItemAttribute.setDescriptionFormat(descriptionFormat);

							String descriptionString = cachedAttribute.get("descriptionString");
							if (descriptionString != null)
								dotaItemAttribute.setDescriptionString(descriptionString);

							dotaItemAttribute.setItem(dotaItem);
							dotaItemAttribute.setUniqueIndex(attributeUniqueIndex);

							dotaItemCatalogDAO.saveItemAttribute(dotaItemAttribute);
							if (!dotaItem.hasAttribute(attributeUniqueIndex)) {
								dotaItem.addAttribute(dotaItemAttribute);
							}
						}
					}
				}

				// dotaItemCatalogDAO.saveItem(dotaItem);

			}

		} catch (Exception e) {
			logger.info(e.toString());
			e.printStackTrace();
		} finally {
		}
	}

	//@Async
	public void retrieveAndStorePlayerItems(Long steamId64) {
		/*
		 * Tries to retrieve all game items from Steam. On success save them to
		 * DB.
		 */
		if (logger.isDebugEnabled()) {
			logger.debug("retrieveAndStorePlayerItems called");
		}

		DotaPlayer player = dotaPlayerCatalogDAO.getPlayerBySteamId64(steamId64);
		if (player == null) {
			logger.info(String.format("Player with Steam ID %s not found.", steamId64));
			return;
		}

		String key = SpringPropertiesUtil.getProperty("global.steam_api_key");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("key", key);
		params.put("language", "en");
		params.put("format", "json");
		params.put("steamid", steamId64.toString());

		String url = String.format("http://api.steampowered.com/IEconItems_570/GetPlayerItems/v%04d/?", steamAPIVersion);

		SteamResponse response = null;
		try {
			response = SteamHttpRequest.sendHttpRequest(url, params);
		} catch (Exception e) {
			logger.error(e);
		}

		if (logger.isDebugEnabled() && response != null) {
			logger.debug(String.format("Response code: %d", response.getResponseCode()));
		}

		if (response.getResponseCode() != 200) {
			return;
		}

		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode rootNode = objectMapper.readTree(response.response);
			JsonNode result = rootNode.get("result");

			JsonNode statusNode = result.get("status");
			String status = statusNode.asText();
			if (status.equals("15")) {
				logger.info("retrieveAndStorePlayerItems: Steam server responded: backpack is private.");
				return;
			} else if (status.equals("18")) {
				logger.info("retrieveAndStorePlayerItems: Steam server responded: steamId does not exist.");
				return;
			} else if (!status.equals("1")) {
				logger.info(String.format("retrieveAndStorePlayerItems: Steam server returned status %s. Stopping.", status));
				return;
			};

			JsonNode itemsNode = result.get("items");
			Iterator<JsonNode> items = itemsNode.getElements();
			
			if (items == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("No player items arrived in response.");
				}
				return; // no point to continue
			}

			while (items.hasNext()) {
				JsonNode item = items.next();
				Integer uniqueIndex = Integer.parseInt(item.get("defindex").asText());

				DotaItem dotaItem = dotaItemCatalogDAO.getItemByUniqueIndex(uniqueIndex);
				if (dotaItem == null) {
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Item %s not found", uniqueIndex));
					}
					// Newest schema not yet fetched from Steam
					continue;
				}

				DotaPlayerItem playerItem = dotaItemCatalogDAO.getPlayerItemByUniqueIndex(player, uniqueIndex);
				if (playerItem == null) {
					playerItem = new DotaPlayerItem();
				}
				playerItem.setDotaPlayer(player);
				playerItem.setDotaItem(dotaItem);
				playerItem.setUniqueIndex(uniqueIndex);

				JsonNode level = item.get("level");
				if (level != null) {
					playerItem.setLevel(Integer.parseInt(level.asText()));
				}

				JsonNode cannotTrade = item.get("flag_cannot_trade");
				if (cannotTrade != null) {
					playerItem.setCannotTrade(cannotTrade.asBoolean());
				} else {
					playerItem.setCannotTrade(false);
				}

				JsonNode cannotCraft = item.get("flag_cannot_craft");
				if (cannotCraft != null) {
					playerItem.setCannotCraft(cannotCraft.asBoolean());
				} else {
					playerItem.setCannotCraft(false);
				}

				JsonNode quality = item.get("quality");
				if (quality != null) {
					DotaItemQuality itemQuality = dotaItemCatalogDAO.getItemQuality(Integer.parseInt(quality.asText()));
					if(itemQuality!=null){
						playerItem.setQuality(itemQuality.getKey());
						playerItem.setQualityDisplay(itemQuality.getValue());
					}
				}

				JsonNode customName = item.get("custom_name");
				if (customName != null) {
					playerItem.setCustomName(customName.asText());
				}

				JsonNode customDescription = item.get("custom_desc");
				if (customDescription != null) {
					playerItem.setCustomDescription(customDescription.asText());
				}
				dotaItemCatalogDAO.savePlayerItem(playerItem);

				JsonNode playerItemAttributes = item.get("attributes");
				if(playerItemAttributes!=null){
					Iterator<JsonNode> playerItemAttributeRecords = playerItemAttributes.getElements();
					while (playerItemAttributeRecords.hasNext()) {
						JsonNode entry = playerItemAttributeRecords.next();

						Integer attributeUniqueIndex = entry.get("defindex").asInt();
						if(attributeUniqueIndex==null) continue;

						DotaPlayerItemAttribute playerItemAttribute = dotaItemCatalogDAO.getPlayerItemAttribute(playerItem, attributeUniqueIndex);
						if(playerItemAttribute==null){
							playerItemAttribute = new DotaPlayerItemAttribute();
						}
						playerItemAttribute.setPlayerItem(playerItem);
						playerItemAttribute.setUniqueIndex(attributeUniqueIndex);

						JsonNode valueNode = entry.get("value");
						if(valueNode!=null){
							String attributeValue = valueNode.asText();
							playerItemAttribute.setValue(attributeValue);
						}

						dotaItemCatalogDAO.savePlayerItemAttribute(playerItemAttribute);
					}
				}

				player.addItem(playerItem);
			}

		} catch (Exception e) {
			logger.info(e.toString());
			e.printStackTrace();
		} finally {
		}
	}
}