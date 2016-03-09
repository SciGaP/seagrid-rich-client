package gamess.IncompatibilityPackage;

import gamess.IncompatibilityPackage.ConditionLogic.Condition;
import gamess.IncompatibilityPackage.ConditionLogic.Or;
import gamess.IncompatibilityPackage.Restriction.RestrictionType;

public class Incompatible extends RestrictionType
{
	private boolean isAvailable = false;
	
	public Incompatible()
	{
		condition = new Or();
	}
	
	public Incompatible(RestrictionType copyRestr)
	{
		condition = new Or();
		this.Input = copyRestr.getInput();
		this.condition.add(copyRestr.getCondition());
	}

	@Override
	public void addCondition(Condition cond) 
	{
		condition.add(cond);
	}
	
	public boolean isAvailable()
	{
		return isAvailable;
	}
	
	public void setAvailable(boolean isAvail)
	{
		isAvailable = isAvail;
	}
}
