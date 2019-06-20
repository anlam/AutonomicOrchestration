package eu.arrowhead.autonomic.orchestrator.manager.plan.model;

public enum PlanStatus
{
	NEW,
	SENDING,
	SENT,
	EXECUTING,
	EXECUTED,
	IGNORED,
	FAILED
}