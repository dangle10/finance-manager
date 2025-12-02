package pack_Project.GUI;

import pack_Project.main;
import pack_Project.DTO.User;

// Lớp con triển khai (Concrete Class)
public class DefaultLoginFrame extends LoginFrame {

    public DefaultLoginFrame(main app) {
        super(app); // Gọi constructor của lớp cha LoginFrame
    }

    // BẮT BUỘC phải triển khai phương thức trừu tượng
    @Override
    protected void onLoginSuccess(User authenticatedUser) {
        // Logic mặc định sau khi đăng nhập thành công
        new DashboardFrame(app, authenticatedUser);
    }
}