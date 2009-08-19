/*
 * Ability.java Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite
 * 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id$
 */
package pcgen.core;


import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.AbilityList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;

/**
 * Definition and games rules for an Ability.
 *
 * @author   ???
 * @version  $Revision$
 */
public final class Ability extends PObject implements Categorisable, CategorizedCDOMObject<Ability>
{
	public static final CDOMReference<AbilityList> FEATLIST;

	static
	{
		AbilityList wpl = new AbilityList();
		wpl.setName("*Feats");
		FEATLIST = CDOMDirectSingleRef.getRef(wpl);
	}

	/**
	 * Get the category of this ability
	 *
	 * @return  The category of this Ability
	 */
	public String getCategory()
	{
		return get(ObjectKey.ABILITY_CAT).getKeyName();
	}

	/**
	 * Set the AbilityType property of this Ability
	 *
	 * @param  type  The type of this ability (normal, automatic, virtual (see
	 *               named constants))
	 */
	public void setAbilityNature(final Nature type)
	{
		if ( type == Nature.ANY )
		{
			return;
		}

		put(ObjectKey.ABILITY_NATURE, type);
	}

	/**
	 * Really badly named method.
	 *
	 * @return  The nature of this feat.
	 */
	public Nature getAbilityNature()
	{
		return getSafe(ObjectKey.ABILITY_NATURE);
	}
	/**
	 * Bog standard clone method
	 *
	 * @return  a copy of this Ability
	 */
	@Override
	public Ability clone()
	{
		try
		{
			return (Ability) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			ShowMessageDelegate.showMessageDialog(e.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
			return null;
		}
	}

	/**
	 * Make a string that can be saved that will represent this Ability object
	 *
	 * @return  a string representation that can be parsed to rebuild the
	 *          Ability
	 */
	@Override
	public String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(getDisplayName());
		txt.append("\tCATEGORY:").append(getCategory());
		txt.append("\t");
		txt.append(StringUtil.joinToStringBuffer(Globals.getContext().unparse(
				this), "\t"));
		txt.append("\t");
		txt.append(PrerequisiteWriter.prereqsToString(this));
		return txt.toString();
	}

	/**
	 * Enhanced containsAssociated, which parses the input parameter for "=",
	 * "+num" and "-num" to extract the value to look for.
	 * @param   type  The type we're looking for
	 *
	 * @return  enhanced containsAssociated, which parses the input parameter
	 *          for "=", "+num" and "-num" to extract the value to look for.
	 */
	@Override
	public int numberInList(PlayerCharacter pc, final String type)
	{
        String aType = type;

        if (aType.lastIndexOf('=') > -1)
		{
			aType = aType.substring(aType.lastIndexOf('=') + 1);
		}

		// truncate at + sign if following character is a number
        final String numString = "0123456789";
        if (aType.lastIndexOf('+') > -1)
		{
			final String aString = aType.substring(aType.lastIndexOf('+') + 1);

			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
			{
				aType = aType.substring(0, aType.lastIndexOf('+'));
			}
		}

		// truncate at - sign if following character is a number
		if (aType.lastIndexOf('-') > -1)
		{
			final String aString = aType.substring(aType.lastIndexOf('-') + 1);

			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
			{
				aType = aType.substring(0, aType.lastIndexOf('-'));
			}
		}

        int iCount = 0;
		for (String assoc : pc.getAssociationList(this))
		{
			if (assoc.equalsIgnoreCase(aType))
			{
				iCount += 1;
			}
		}

		return iCount;
	}

    /**
     * Compare an ability (category) to another one
     * Returns the compare value from String.compareToIgnoreCase
     * 
     * @param obj the object that we're comparing against
     * @return compare value
     */
	@Override
	public int compareTo(final Object obj)
	{
		if (obj != null)
		{
			try
			{
				final Ability ab = (Ability) obj;
				Category<Ability> cat = getCDOMCategory();
				Category<Ability> othercat = ab.getCDOMCategory();
				if (cat == null && othercat != null)
				{
					return -1;
				}
				else if (cat != null && othercat == null)
				{
					return 1;
				}
				else if (cat != null)
				{
					int diff = cat.getKeyName().compareTo(othercat.getKeyName());
					if (diff != 0)
					{
						return diff;
					}
				}
			}
			catch (ClassCastException e)
			{
				// Do nothing.  If the cast to Ability doesn't work, we assume that
				// the category of the Object passed in matches the category of this
				// Ability and compare KeyNames
			}

			// this should throw a ClassCastException for non-PObjects, like the
			// Comparable interface calls for
			return this.getKeyName().compareToIgnoreCase(((PObject) obj).getKeyName());
		}
		return 1;
	}

	/**
     * Equals function, uses compareTo to do the work
     * 
	 * @param other Ability to compare to
	 * @return true if they are equal
	 */
    @Override
	public boolean equals(final Object other)
	{
		return other instanceof Ability && this.compareTo(other) == 0;
	}
    
    /**
     * Must be consistent with equals
     */
    @Override
	public int hashCode() {
    	//Can't be more complicated because the weird nature of compareTo
    	return getKeyName().hashCode();
    }

	public Category<Ability> getCDOMCategory()
	{
		return get(ObjectKey.ABILITY_CAT);
	}

	public void setCDOMCategory(Category<Ability> cat)
	{
		put(ObjectKey.ABILITY_CAT, cat);
	}
	
	@Override
	public ListKey<Description> getDescriptionKey()
	{
		if (SettingsHandler.useFeatBenefits() && containsListFor(ListKey.BENEFIT))
		{
			return ListKey.BENEFIT;
		}
		return ListKey.DESCRIPTION;
	}
}
