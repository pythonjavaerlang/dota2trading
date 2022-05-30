package in.dota2.model;

import org.springframework.dao.DataAccessException;

public interface DotaImageCatalogDAOInterface {

	DotaImage getImageById(Integer imageId) throws DataAccessException;
	DotaImage getImageBySha1(String sha1) throws DataAccessException;
	DotaImage saveImage(DotaImage dotaImage) throws DataAccessException;
	void deleteImage(DotaImage dotaImage) throws DataAccessException;

}