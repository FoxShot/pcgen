/*
 * Copyright (c) Thomas Parker, 2015.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import java.util.List;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VarScoped;
import pcgen.base.formula.base.VariableID;
import pcgen.base.solver.AggressiveSolverManager;
import pcgen.base.solver.ProcessStep;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractItemFacet;

/**
 * This stores the AggressiveSolverManager for each PlayerCharacter.
 */
public class SolverManagerFacet extends
		AbstractItemFacet<CharID, AggressiveSolverManager>
{
	private VariableLibraryFacet variableLibraryFacet;

	private ScopeFacet scopeFacet;

	public <T> List<ProcessStep<T>> diagnose(CharID id, VariableID<T> varID)
	{
		return get(id).diagnose(varID);
	}

	public <T> void addModifier(CharID id, VarModifier<T> vm, VarScoped target,
		ScopeInstance source)
	{
		ScopeInstance scope = scopeFacet.get(id, vm.getLegalScope().getName(), target);
		VariableID<T> varID =
				(VariableID<T>) variableLibraryFacet.getVariableID(
					id.getDatasetID(), scope, vm.getVarName());
		get(id).addModifier(varID, vm.getModifier(), source);
	}

	public <T> void removeModifier(CharID id, VarModifier<T> vm,
		VarScoped target, ScopeInstance source)
	{
		ScopeInstance scope = scopeFacet.get(id, vm.getLegalScope().getName(), target);
		VariableID<T> varID =
				(VariableID<T>) variableLibraryFacet.getVariableID(
					id.getDatasetID(), scope, vm.getVarName());
		get(id).removeModifier(varID, vm.getModifier(), source);
	}

	public void setVariableLibraryFacet(
		VariableLibraryFacet variableLibraryFacet)
	{
		this.variableLibraryFacet = variableLibraryFacet;
	}

	public void setScopeFacet(ScopeFacet scopeFacet)
	{
		this.scopeFacet = scopeFacet;
	}

}
