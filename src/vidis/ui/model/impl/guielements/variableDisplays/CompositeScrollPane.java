package vidis.ui.model.impl.guielements.variableDisplays;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import vidis.data.var.AVariable;
import vidis.data.var.IVariableChangeListener;
import vidis.data.var.IVariableContainer;
import vidis.data.var.vars.DefaultVariable;
import vidis.data.var.vars.FieldVariable;
import vidis.data.var.vars.MethodVariable;
import vidis.ui.model.impl.BasicGuiContainer;
import vidis.ui.model.impl.guielements.scrollpane.AScrollpane3D;

public class CompositeScrollPane extends AScrollpane3D implements IVariableChangeListener {
	private static Logger logger = Logger.getLogger(CompositeScrollPane.class);
	
	private IVariableContainer vcontainer;
	
	private Map<String, BasicGuiContainer> mapVariableContainer;
	private Map<String, GroupDisplay> nameSpacesVisibility;
	
	public CompositeScrollPane ( IVariableContainer vcontainer ) {
		super();
		setUseScissorTest( true );
		mapVariableContainer = new HashMap<String, BasicGuiContainer>();
		nameSpacesVisibility = new HashMap<String, GroupDisplay>();
		this.vcontainer = vcontainer;
		
		vcontainer.addVariableChangeListener( this );
	}

	private BasicGuiContainer createNewDisplay( String id ) {
		AVariable var = this.vcontainer.getVariableById( id );
		Display ret = DisplayRegistry.createDisplay( var );
		ret.setOpaque( false );
		return ret;
	}
	
	public void variableAdded(String id) {
		if( mapVariableContainer.containsKey(id) ) {
			// do nothing dude
		} else {
			BasicGuiContainer varContainer = createNewDisplay( id );
			mapVariableContainer.put( id, varContainer );
//			this.addChild( varContainer );
			sortVariables();
		}
		
	}
	
	private void sortVariables() {
		try {
			// sort namespaces
			for ( String id : mapVariableContainer.keySet() ) {
				String ns = AVariable.getNamespace( id );
				if ( !nameSpacesVisibility.containsKey( ns ) ) {
					GroupDisplay gd = new GroupDisplay( ns, true );
					gd.setBackColor( Color.black );
					gd.addContent( mapVariableContainer.get( id ) );
					nameSpacesVisibility.put( ns, gd );
				}
				else {
					nameSpacesVisibility.get( ns ).addContent( mapVariableContainer.get(id) );
				}
			}
			// remove all childs
			this.removeAllChilds();
			// add everything together
			for ( GroupDisplay gd : this.nameSpacesVisibility.values() ) {
				this.addChild( gd );
				for ( BasicGuiContainer bgc : gd.getContent() ) {
					this.addChild( bgc );
				}
			}
		}
		catch ( Exception e ) {
			logger.error( "error in sortVariables() :", e );
		}
	}
	
	
	

	public void variableChanged(String id) {
		variableAdded(id);
		
	}

	public void variableRemoved(String id) {
		if( mapVariableContainer.containsKey(id) ) {
			this.removeChild( mapVariableContainer.remove( id ) );
		}
	}
}
