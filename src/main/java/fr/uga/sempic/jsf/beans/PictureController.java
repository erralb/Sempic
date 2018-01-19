package fr.uga.sempic.jsf.beans;

import fr.uga.miashs.sempic.model.Picture;
import fr.uga.miashs.sempic.model.datalayer.PictureFacade;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import fr.uga.miashs.sempic.model.util.JsfUtil;
import fr.uga.miashs.sempic.model.util.PaginationHelper;
import fr.uga.miashs.sempic.rdf.Namespaces;
import fr.uga.miashs.sempic.rdf.RDFStore;
import java.io.File;
import java.io.InputStream;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.ManagedBean;
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
import javax.faces.model.SelectItemGroup;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDFS;

@ManagedBean
@Named("pictureController")
@SessionScoped
public class PictureController implements Serializable {

	private Picture current;
	private DataModel items = null;
	
	private Part file;
	
	private Resource rdfPhoto;
	public Resource getRdfPhoto() {
		return rdfPhoto;
	}
	public void setRdfPhoto(Resource rdfPhoto) {
		this.rdfPhoto = rdfPhoto;
	}
	
	private String rdfResources;
	public String getRdfResources() {
		return rdfResources;
	}
	public void setRdfResources(String rdfResources) {
		this.rdfResources = rdfResources;
	}
	
	private RDFStore rdfs = new RDFStore();
	private String[] persons;
	private String[] places;
	private String[] depicts;
	private String[] takenBy;
	private String[] takenIn;
	private String takenWhen;

	private ArrayList<SelectItem> rdfSelect;
	
	
	private String ids = "";
	
    @Inject
    private AuthManager auth;
	
	@EJB
	private fr.uga.miashs.sempic.model.datalayer.PictureFacade ejbFacade;
	private PaginationHelper pagination;
	private int selectedItemIndex;
	
	public PictureController() {
	}
	

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
	
	public String[] getPlaces() {
		return places;
	}

	public void setPlaces(String[] places) {
		this.places = places;
	}
	
	public RDFStore getRdfs() {
		return rdfs;
	}
	public void setRdfs(RDFStore rdfs) {
		this.rdfs = rdfs;
	}
	public String[] getTakenIn() {
		return takenIn;
	}
	public void setTakenIn(String[] takenIn) {
		this.takenIn = takenIn;
	}

	public ArrayList<SelectItem> getRdfSelect() {
		return rdfSelect;
	}

	public void setRdfSelect(ArrayList<SelectItem> rdfSelect) {
		this.rdfSelect = rdfSelect;
	}

	public String getTakenWhen() {
		return takenWhen;
	}

	public void setTakenWhen(String takenWhen) {
			this.takenWhen = takenWhen;
	}

	public String[] getPersons() {
		return persons;
	}

	public void setPersons(String[] persons) {
		this.persons = persons;
	}
	
	public String[] getDepicts() {
		return depicts;
	}

	public void setDepicts(String[] depicts) {
		this.depicts = depicts;
	}

	public String[] getTakenBy() {
		return takenBy;
	}

	public void setTakenBy(String[] takenBy) {
		this.takenBy = takenBy;
	}

	public Picture getSelected() {
		if (current == null) {
			current = new Picture();
			selectedItemIndex = -1;
		}
		return current;
	}

	private PictureFacade getFacade() {
		return ejbFacade;
	}

	public PaginationHelper getPagination() {
		if (pagination == null) {
			pagination = new PaginationHelper(10) {

				@Override
				public int getItemsCount() {
					return getFacade().count();
				}

				@Override
				public DataModel createPageDataModel() {
					return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
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
		current = (Picture) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
		
//		System.out.println("Here "+current.getId());
		Resource photoRes = rdfs.readPhoto(current.getId());
		
		StmtIterator itr;
		rdfResources = "";
		itr = photoRes.listProperties(SempicOnto.inThePicture);
		rdfResources += "<h3>inThePicture</h3>";
		while(itr.hasNext())
		{
			rdfResources += rdfs.getStatementHash(itr.next())+"<br/>";
		}
		itr = photoRes.listProperties(SempicOnto.depicts);
		rdfResources += "<h3>depicts</h3>";
		while(itr.hasNext())
		{
			rdfResources += rdfs.getStatementHash(itr.next())+"<br/>";
		}
		itr = photoRes.listProperties(SempicOnto.takenIn);
		rdfResources += "<h3>takenIn</h3>";
		while(itr.hasNext())
		{
			rdfResources += rdfs.getStatementHash(itr.next())+"<br/>";
		}
		itr = photoRes.listProperties(SempicOnto.takenBy);
		rdfResources += "<h3>takenBy</h3>";
		while(itr.hasNext())
		{
			rdfResources += rdfs.getStatementHash(itr.next())+"<br/>";
		}
		itr = photoRes.listProperties(SempicOnto.takenWhen);
		rdfResources += "<h3>takenWhen</h3>";
		while(itr.hasNext())
		{
			rdfResources += rdfs.getStatementHash(itr.next())+"<br/>";
		}
		
//		rdfResources = "inThePicture "+photoRes.getProperty(SempicOnto.inThePicture).getProperty(RDFS.label).getLiteral().toString()+"<br/>";
//		rdfResources += "Depicts "+photoRes.getProperty(SempicOnto.depicts).getProperty(RDFS.label).getLiteral().toString()+"<br/>";
//		rdfResources += "takenBy "+photoRes.getProperty(SempicOnto.takenBy).getProperty(RDFS.label).getLiteral().toString()+"<br/>";
//		rdfResources += "takenIn "+photoRes.getProperty(SempicOnto.takenIn).getProperty(RDFS.label).getLiteral().toString()+"<br/>";
//		rdfResources += "takenWhen "+photoRes.getProperty(SempicOnto.takenWhen).getProperty(RDFS.label).getLiteral().toString()+"<br/>";
//		rdfResources = rdfs.getPhotoModel(current.getId()).toString();
		
		return "View";
	}

	public String prepareCreate() {
		current = new Picture();
		selectedItemIndex = -1;
		return "Create";
	}

	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}
	
	/**
	 * Lists Places
	 * @return ArrayList<SelectItem> the items for f:selectItems in the views
	 */
    public ArrayList<SelectItem> rdfPlaceSelect() {
		rdfSelect = new ArrayList<SelectItem>();
		rdfSelect.add(buildSelectItems("Places",SempicOnto.Place, true));
		return rdfSelect;
    }
	
	/**
	 * Lists People
	 * @return ArrayList<SelectItem> the items for f:selectItems in the views
	 */
    public ArrayList<SelectItem> rdfPeopleSelect() {
		rdfSelect = new ArrayList<SelectItem>();
		rdfSelect.add(buildSelectItems("People",SempicOnto.Person, true));
		return rdfSelect;
    }
	
	/**
	 * Lists depiction resources
	 * @return ArrayList<SelectItem> the items for f:selectItems in the views
	 */
    public ArrayList<SelectItem> rdfContextSelect() {
		rdfSelect = new ArrayList<SelectItem>();
		rdfSelect.add(buildSelectItems("Animals",SempicOnto.Animal, false));
		rdfSelect.add(buildSelectItems("Person",SempicOnto.Person, false));
		rdfSelect.add(buildSelectItems("Place",SempicOnto.Place, false));
		return rdfSelect;
    }
	
	/**
	 * Creates SelectItems group
	 * @param groupName
	 * @param res the resource to add
	 * @return SelectItemGroup
	 */
	public SelectItemGroup buildSelectItems(String groupName, Resource res, boolean instances)
	{
		//Get subclasses
		
		List<Resource> resources;
		ArrayList<SelectItem> selectItems= new ArrayList<SelectItem>();
		if(! instances)
		{
			resources = rdfs.listSubClassesOf(res);
			//Build List
			resources.forEach(i -> {
				selectItems.add(new SelectItem(i, i.getProperty(RDFS.label).getLiteral().toString()));
			});
		}
		else
		{
			//@TODO: something is still wrong here, I can't extract the label from the resource
			resources = rdfs.listInstancesOf(res);
			//Build List
			resources.forEach(i -> {
				selectItems.add(new SelectItem(i, i.getProperty(RDFS.label).getLiteral().toString()));
			});
		}
		
		//sort
		selectItems.sort((o1, o2) -> {
			return o1.getLabel().compareTo(o2.getLabel());
		});
		//list to array
		SelectItem[] selectItemsArray= selectItems.toArray(new SelectItem[selectItems.size()]);
		//Create itemGroup and add elements
		SelectItemGroup itemGroup = new SelectItemGroup(groupName);
		itemGroup.setSelectItems(selectItemsArray);

		return itemGroup;
	}
	
	public String create() {
		try {
			
			//Save Picture Model
			current.setUser(auth.currentUser());//set logged user as Album owner
			current.setAdded(new Timestamp(System.currentTimeMillis()));
			current.setFilename(file.getSubmittedFileName());
			getFacade().create(current);
			
			//Save File
			//From https://stackoverflow.com/questions/18664579/recommended-way-to-save-uploaded-files-in-a-servlet-application
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			File uploads = new File(ctx.getInitParameter("upload.location"));
			File newFile = new File(uploads, file.getSubmittedFileName());
			try (InputStream input = file.getInputStream()) {
				Files.copy(input, newFile.toPath());
			}
			//If we want to make sur it's not overwriting, but for some reasons generates an error
//			File tmpfile = File.createTempFile("somefilename-", ".ext", uploads);
//			try (InputStream input = file.getInputStream()) {
//				Files.copy(input, tmpfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//			}
			
			//Save RDF
			rdfs = new RDFStore();
			// create an empty RDF graph
			Model m = ModelFactory.createDefaultModel();
			// create an instance of Photo in Model m
			Resource photoRes = m.createResource(Namespaces.getPhotoUri(current.getId()), SempicOnto.Photo);
			//Assign album and owner id
			photoRes.addLiteral(SempicOnto.albumId, current.getAlbum().getId());
			photoRes.addLiteral(SempicOnto.ownerId, auth.currentUser().getId());

			
			//Individuals inThePicture (Pierre, Medor, etc.)
			for (String s: persons) {           
				photoRes.addProperty(SempicOnto.inThePicture, m.getResource(s));
			}

			//Things depicted (Person, Animals, Building)
			for (String s: depicts) {           
				photoRes.addProperty(SempicOnto.depicts, m.getResource(s));
			}
			
//			//takenBy : who took the picture
			for (String s: takenBy) {           
				photoRes.addProperty(SempicOnto.takenBy, m.getResource(s));
			}
//			
			//takenIn : where was the picture taken
			for (String s: takenIn) {           
				photoRes.addProperty(SempicOnto.takenIn, m.getResource(s));
			}

			//takenWhen : picture date
//			timestamp.addLiteral(RDFS.label, takenWhen);
			photoRes.addLiteral(SempicOnto.takenWhen, takenWhen);
//
			m.write(System.out, "turtle");
			
			rdfs.saveModel(m);
			
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PictureCreated"));
			
			recreatePagination();
			recreateModel();
			
			return prepareCreate();
			
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, "Error. Message : "+ e.getMessage());
			return null;
		}
	}
	
	/**
	 * Search the RDF and display result the List.xhtml
	 */
	public String search()
	{
		try {
			//Search picture IDs linked with selected resource
			ids = "";
			setIDs(SempicOnto.inThePicture,persons);
			setIDs(SempicOnto.inThePicture,depicts);
			setIDs(SempicOnto.inThePicture,takenIn);
			setIDs(SempicOnto.inThePicture,takenBy);
			
			//if some IDs were found, launch an SQL query and assign them to items
			System.out.println(ids);
			if(!ids.isEmpty())
			{
				ids = ids.substring(0, ids.length() - 1); //remove last ,
				items = new ListDataModel(getFacade().findAllById(ids));//get items
			}
			else items = null;

			return "List";
			
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, "Error. Message : "+ e.getMessage());
			return null;
		}
	}
	
	/**
	 * Set the Picture IDs we'll use for the search
	 * @param onto The current resource we want search
	 * @param values The resource values
	 */
	public void setIDs(Resource onto, String[] values)
	{
		if(values.length > 0)
		{
			String query = "SELECT ?p WHERE {";
			query += "?p <"+onto+"> ?o. FILTER (";
			boolean first = true;
			for (String s: values) {           
	//				query += "?p <"+SempicOnto.inThePicture+"> <"+s+">.";
				if(!first) query += "|| ";
				first = false;
				query += " ?o = <"+s+"> ";
			}
			query += ")}";

	//		System.out.println("Here query : "+query);

			rdfs.getCnx().querySelect( query, (qs)->{
				Resource subject = qs.getResource("p") ;
				String uri = subject.toString();
				ids += uri.substring(uri.lastIndexOf("/") + 1)+",";
			 }) ;
		}
	}

	public String prepareEdit() {
		current = (Picture) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
		
		Resource photoRes = rdfs.readPhoto(current.getId());
		
		StmtIterator itr;
		rdfResources = "";
		
		itr = photoRes.listProperties(SempicOnto.inThePicture);
		List<Statement> itrList = itr.toList();
		persons = new String[itrList.size()];
		int i = 0;
		while(itr.hasNext())
		{
			persons[i] = itr.next().toString();
		}
		System.out.println(persons);
		itr = photoRes.listProperties(SempicOnto.depicts);
		while(itr.hasNext())
		{
			rdfResources += rdfs.getStatementHash(itr.next())+"<br/>";
		}
		itr = photoRes.listProperties(SempicOnto.takenIn);
		while(itr.hasNext())
		{
			rdfResources += rdfs.getStatementHash(itr.next())+"<br/>";
		}
		itr = photoRes.listProperties(SempicOnto.takenBy);
		while(itr.hasNext())
		{
			rdfResources += rdfs.getStatementHash(itr.next())+"<br/>";
		}
		itr = photoRes.listProperties(SempicOnto.takenWhen);
		while(itr.hasNext())
		{
			rdfResources += rdfs.getStatementHash(itr.next())+"<br/>";
		}
		
		return "Edit";
	}

	public String update() {
		try {
			getFacade().edit(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PictureUpdated"));
			return "View";
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String destroy() {
		current = (Picture) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
		rdfs.deleteModel(rdfs.getPhotoModel(current.getId()));
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
			//Remove from db
			getFacade().remove(current);
			
			//delete file
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String filename = current.getFilename();
			Path fileToDeletePath = Paths.get(ctx.getInitParameter("upload.location")+File.separator+filename);
			Files.delete(fileToDeletePath);
			//delete RDF

			rdfs.deleteModel(rdfs.getPhotoModel(current.getId()));

			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PictureDeleted"));
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
		if (items == null) {
			items = getPagination().createPageDataModel();
//			System.out.print("Items null");
		}
		return items;
	}
	
	public DataModel getUserItems() {
		if (items == null) {
			items = getPagination().createPageDataModel();
			System.out.print("Items null");
		}
		return items;
//		return new ListDataModel(getFacade().findAllByUser());
	}

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

	public Picture getPicture(java.lang.Long id) {
		return ejbFacade.find(id);
	}

	@FacesConverter(forClass = Picture.class)
	public static class PictureControllerConverter implements Converter {

		@Override
		public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
			if (value == null || value.length() == 0) {
				return null;
			}
			PictureController controller = (PictureController) facesContext.getApplication().getELResolver().
					getValue(facesContext.getELContext(), null, "pictureController");
			return controller.getPicture(getKey(value));
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
			if (object instanceof Picture) {
				Picture o = (Picture) object;
				return getStringKey(o.getId());
			} else {
				throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Picture.class.getName());
			}
		}

	}

}
