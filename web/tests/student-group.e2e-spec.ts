import {
  createGrade,
  createStudent,
  createSubject,
  createTag,
  getGrades,
  getStudents,
  getSubjects,
  getTags,
  type GradeRequestDto,
  type StudentRequestDto,
  type SubjectRequestDto,
  type TagRequestDto,
} from '$lib/sdk/fetch-client';
import { deleteGradeEntry, deleteStudentEntry, deleteSubjectEntry, deleteTagEntry } from '$lib/utils/resources';
import { expect, test, type Page } from '@playwright/test';

let page: Page;

test.describe.configure({ mode: 'serial' });

test.describe('Student group page', () => {
  test.beforeAll('delete existing groups', async ({ browser }) => {
    page = await browser.newPage();
    await page.goto('http://localhost:5173/');
    await page.getByRole('link').first().click();
    await page.getByRole('link', { name: 'Gruppe' }).click();
    await expect(page).toHaveURL('http://localhost:5173/admin/studentGroups');
    while (await page.getByText('Keine Gruppen vorhanden.').isHidden()) {
      await page.locator('.flex > .flex > button:nth-child(2)').first().click();
    }
    const students = await getStudents({}).then(({ content }) => content ?? []);
    await deleteStudentEntry(students.map((student) => student.id));
    const grades = await getGrades({});
    await deleteGradeEntry(grades.map((grade) => grade.id));
    const tags = await getTags({});
    await deleteTagEntry(tags.map((tag) => tag.id));
    const subjects = await getSubjects({});
    await deleteSubjectEntry(subjects.map((subject) => subject.id));
    for (let i = 0; i < 25; i++) {
      const requestDto: StudentRequestDto = {
        firstName: `Max${i}`,
        lastName: `Mustermann${i}`,
        groupIds: [],
        tagIds: [],
      };
      await createStudent(requestDto);
    }
  });

  test('empty create', async () => {
    await page.getByRole('button', { name: 'Hinzufügen' }).click();
    await expect(page.locator('body')).toContainText('Es müssen Grades vorhanden sein.');
    await expect(page.locator('body')).toContainText('Keine Fächer vorhanden.');
    await expect(page.locator('body')).toContainText('Keine Tags vorhanden.');
    await expect(page.getByText('Speichern')).toBeDisabled();
    await page.getByRole('button').first().click();
  });

  test('create', async () => {
    for (let i = 0; i < 5; i++) {
      const subjectRequestDto: SubjectRequestDto = { name: `subject${i}`, tagIds: [] };
      await createSubject(subjectRequestDto);
      const tagRequestDto: TagRequestDto = { tagName: `tag${i}` };
      await createTag(tagRequestDto);
      const gradeRequestDto: GradeRequestDto = { name: `grade${i}`, studentGroupIds: [], tagIds: [] };
      await createGrade(gradeRequestDto);
    }
    await page.getByRole('button', { name: 'Hinzufügen' }).click();
    await page.getByRole('textbox').click();
    await page.getByRole('textbox').fill('Group1');
    await page.locator('button').filter({ hasText: 'Ressource auswählen' }).click();
    await page.getByRole('option', { name: 'grade2' }).click();
    await page.getByRole('checkbox').nth(3).click();
    await page.getByRole('checkbox').nth(1).click();
    await page.getByRole('checkbox').nth(4).click();
    await page.getByRole('combobox').click();
    await page.getByRole('option', { name: 'subjects1' }).click();
    await page.getByRole('option', { name: 'subjects2' }).click();
    await page.getByRole('combobox').click();
    await page.getByText('Speichern').click();
    await expect(page.getByText('Keine Gruppen vorhanden.')).toBeHidden();
    await expect(page.getByText('Group1')).toBeVisible();
  });

  test('create no selected', async () => {
    await page.getByRole('button', { name: 'Hinzufügen', exact: true }).click();
    await page.getByRole('button', { name: 'Speichern' }).click();
    await expect(page.locator('body')).toContainText('Dieses Feld darf nicht leer sein.');
    await expect(page.locator('body')).toContainText('Es muss eine Stufe ausgewählt werden.');
    await page.getByRole('button').first().click();
    await page.getByRole('button').first().click();
  });
});
