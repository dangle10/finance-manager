package pack_Project.GUI;

import pack_Project.main;
import pack_Project.DTO.User;

public class DefaultLoginFrame extends LoginFrame {

    public DefaultLoginFrame(main app) {
        super(app);
    }

    @Override
    protected void onLoginSuccess(User authenticatedUser) {
       
        new DashboardFrame(app, authenticatedUser);
    }

}
