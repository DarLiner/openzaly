package com.akaxin.site.business.api;

import com.akaxin.common.command.Command;
import com.akaxin.common.command.CommandResponse;

public interface IRequest {

	public CommandResponse process(Command command);

}
