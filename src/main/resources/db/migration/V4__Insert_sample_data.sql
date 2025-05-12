INSERT INTO category (id, name, version) VALUES
(1, 'Electronics', 1),
(2, 'Books', 1),
(3, 'Office Supplies', 1);

INSERT INTO product (name, description, price, quantity, version, category_id) VALUES
('Laptop Pro', 'High-performance laptop for professionals', 1200.00, 50, 1, 1),
('Wireless Mouse', 'Ergonomic wireless mouse', 25.50, 200, 1, 1),
('Mechanical Keyboard', 'RGB mechanical keyboard with blue switches', 75.00, 100, 1, 1);

INSERT INTO product (name, description, price, quantity, version, category_id) VALUES
('Harry Potter', 'Kids running around with sticks', 12.00, 11, 1, 2),
('Star Wars', 'Not the best family relationships of some father', 25.50, 22, 1, 2),
('Clean Code', 'A book which allows you to roast everybody''s code', 75.00, 33, 1, 2);

INSERT INTO product (name, description, price, quantity, version, category_id) VALUES
('Pencil', 'When your keyboard stops working', 5.00, 50, 1, 3),
('Notebook', 'In case you have no electricity', 15.50, 200, 1, 3),
('Rubber Duck', 'Better pair coding than with anybody', 100.00, 100, 1, 3);

INSERT INTO user_roles (id, role) VALUES
(1, 'FULL'),
(2, 'READER');

-- BCrypt hash for 'password'
INSERT INTO users (id, username, password, role_id) VALUES
(1, 'admin', '$2a$10$GBcgm3rxusrlYJ3IUu1Pr.5u2koK8OLWHxIku3RRGGhTfdsTApzqC', 1),
(2, 'user', '$2a$10$GBcgm3rxusrlYJ3IUu1Pr.5u2koK8OLWHxIku3RRGGhTfdsTApzqC', 2);
