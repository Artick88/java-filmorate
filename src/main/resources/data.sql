--��������
INSERT INTO MPA("name", "description")
SELECT 'G', '��� ���������� �����������' WHERE NOT EXISTS (SELECT 1 FROM MPA WHERE "name" = 'G');
INSERT INTO MPA("name", "description")
SELECT 'PG', '������������� ����������� ���������' WHERE NOT EXISTS (SELECT 1 FROM MPA WHERE "name" = 'PG');
INSERT INTO MPA("name", "description")
SELECT 'PG-13', '����� �� 13 ��� �������� �� ���������' WHERE NOT EXISTS (SELECT 1 FROM MPA WHERE "name" = 'PG-13');
INSERT INTO MPA("name", "description")
SELECT 'R', '����� �� 17 ��� ����������� ����������� ���������' WHERE NOT EXISTS (SELECT 1 FROM MPA WHERE "name" = 'R');
INSERT INTO MPA("name", "description")
SELECT 'NC-17', '����� �� 18 ��� �������� ��������' WHERE NOT EXISTS (SELECT 1 FROM MPA WHERE "name" = 'NC-17');

--�����
INSERT INTO "genre" ("code", "name", "description")
SELECT 'comedy', '�������', '�������' WHERE NOT EXISTS (SELECT 1 FROM "genre" WHERE "code" = 'comedy');
INSERT INTO "genre" ("code", "name", "description")
SELECT 'drama', '�����', '�����' WHERE NOT EXISTS (SELECT 1 FROM "genre" WHERE "code" = 'drama');
INSERT INTO "genre" ("code", "name", "description")
SELECT 'cartoon', '����������', '����������' WHERE NOT EXISTS (SELECT 1 FROM "genre" WHERE "code" = 'cartoon');
INSERT INTO "genre" ("code", "name", "description")
SELECT 'thriller', '�������', '�������' WHERE NOT EXISTS (SELECT 1 FROM "genre" WHERE "code" = 'thriller');
INSERT INTO "genre" ("code", "name", "description")
SELECT 'documentary', '��������������', '�������������� �����' WHERE NOT EXISTS (SELECT 1 FROM "genre" WHERE "code" = 'documentary');
INSERT INTO "genre" ("code", "name", "description")
SELECT 'action', '������', '������, �����' WHERE NOT EXISTS (SELECT 1 FROM "genre" WHERE "code" = 'action');

--������ ������ � ������
INSERT INTO "status_type" ("code", "name", "description")
      select 'Not_Approved', '�� ������������', '������ �� ������ �� ������������ �����������' WHERE NOT EXISTS (SELECT 1 FROM "status_type" WHERE "code" = 'Not_Approved')
union select 'Approved', '������������', '������ �� ������ ������������ �����������' WHERE NOT EXISTS (SELECT 1 FROM "status_type" WHERE "code" = 'Approved');