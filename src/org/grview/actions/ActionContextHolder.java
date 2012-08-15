package org.grview.actions;

public interface ActionContextHolder<T1 extends AbstractEditAction<?>, T2 extends AsinActionSet<T1>>
{
	public abstract AsinActionContext<T1, T2> getActionContext();
}
