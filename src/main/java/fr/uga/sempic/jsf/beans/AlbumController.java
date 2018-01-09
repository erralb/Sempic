package fr.uga.sempic.jsf.beans;

import fr.uga.miashs.sempic.model.Album;
import fr.uga.miashs.sempic.model.datalayer.AlbumFacade;
import fr.uga.miashs.sempic.model.util.JsfUtil;
import fr.uga.miashs.sempic.model.util.PaginationHelper;

import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.List;

@Named("albumController")
@SessionScoped
public class AlbumController implements Serializable {

	private Album current;
	private DataModel items = null;
	
    @Inject
    private AuthManager auth;
	
	@EJB
	private fr.uga.miashs.sempic.model.datalayer.AlbumFacade ejbFacade;
	private PaginationHelper pagination;
	private int selectedItemIndex;

	public AlbumController() {
	}

	public Album getSelected() {
		if (current == null) {
			current = new Album();
			selectedItemIndex = -1;
		}
		return current;
	}

	private AlbumFacade getFacade() {
		return ejbFacade;
	}

	public PaginationHelper getPagination() {
		if (pagination == null) {
			pagination = new PaginationHelper(10) {
				
				List<Album> res;
				
				{
					res = getFacade().findAllByUser();
				}
				
				@Override
				public int getItemsCount() {
//					return getFacade().count();
					return res.size();
				}

				@Override
				public DataModel createPageDataModel() {
//					return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
					return new ListDataModel(res.subList(getPageFirstItem(), Math.min(getPageFirstItem() + getPageSize(),getItemsCount())));
				}
			};
		}
		return pagination;
	}

	public String prepareList() {
		recreateModel();
		return "List";
	}

	public String prepareView() {
		current = (Album) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
		return "View";
	}

	public String prepareCreate() {
		current = new Album();			
		recreateModel();	
		recreatePagination();
		selectedItemIndex = -1;
		return "Create";
	}

	public String create() {
		try {
			current.setUser(auth.currentUser());//set logged user as Album owner
			current.setCreated(new Timestamp(System.currentTimeMillis()));
			getFacade().create(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AlbumCreated"));
			return prepareCreate();
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String prepareEdit() {
		current = (Album) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();		
		recreateModel();	
		recreatePagination();
		return "Edit";
	}

	public String update() {
		try {
			getFacade().edit(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AlbumUpdated"));
			return "View";
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String destroy() {
		current = (Album) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
		performDestroy();
		recreatePagination();
		recreateModel();
		return "List";
	}

	public String destroyAndView() {
		performDestroy();
		recreateModel();
		updateCurrentItem();
		if (selectedItemIndex >= 0) {
			return "View";
		} else {
			// all items were removed - go back to list
			recreateModel();
			return "List";
		}
	}

	private void performDestroy() {
		try {
			getFacade().remove(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AlbumDeleted"));
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
		}
	}

	private void updateCurrentItem() {
		int count = getFacade().count();
		if (selectedItemIndex >= count) {
			// selected index cannot be bigger than number of items:
			selectedItemIndex = count - 1;
			// go to previous page if last page disappeared:
			if (pagination.getPageFirstItem() >= count) {
				pagination.previousPage();
			}
		}
		if (selectedItemIndex >= 0) {
			current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
		}
	}

	public DataModel getItems() {
//		if (items == null) {
			items = getPagination().createPageDataModel();
//		}
		return items;
	}

//	public static DataModel getAllAlbums() {
//			items = getPagination().createPageDataModel();
//		return items;
//	}

	private void recreateModel() {
		items = null;
	}

	private void recreatePagination() {
		pagination = null;
	}

	public String next() {
		getPagination().nextPage();
		recreateModel();
		return "List";
	}

	public String previous() {
		getPagination().previousPage();
		recreateModel();
		return "List";
	}

	public SelectItem[] getItemsAvailableSelectMany() {
		return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
	}

	public SelectItem[] getItemsAvailableSelectOne() {
		return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
	}

	public Album getAlbum(java.lang.Long id) {
		return ejbFacade.find(id);
	}

	@FacesConverter(forClass = Album.class)
	public static class AlbumControllerConverter implements Converter {

		@Override
		public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
			if (value == null || value.length() == 0) {
				return null;
			}
			AlbumController controller = (AlbumController) facesContext.getApplication().getELResolver().
					getValue(facesContext.getELContext(), null, "albumController");
			return controller.getAlbum(getKey(value));
		}

		java.lang.Long getKey(String value) {
			java.lang.Long key;
			key = Long.valueOf(value);
			return key;
		}

		String getStringKey(java.lang.Long value) {
			StringBuilder sb = new StringBuilder();
			sb.append(value);
			return sb.toString();
		}

		@Override
		public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
			if (object == null) {
				return null;
			}
			if (object instanceof Album) {
				Album o = (Album) object;
				return getStringKey(o.getId());
			} else {
				throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Album.class.getName());
			}
		}

	}

}
