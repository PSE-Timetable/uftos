import {
  createGrade,
  createStudent,
  createTeacher,
  deleteGrades,
  deleteRooms,
  deleteStudentGroup,
  deleteStudents,
  deleteTeachers,
  getGrades,
  getRooms,
  getStudentGroups,
  getStudents,
  getTeachers,
} from '$lib/sdk/fetch-client';
import { expect, test } from '@playwright/test';

test.describe('Admin Overview', () => {
  test('shows statistics', async ({ page }) => {
    const totalStudents = await getStudents({ page: 0 }).then(({ totalElements }) => totalElements);
    const students = await getStudents({ page: 0, size: totalStudents }).then(({ content }) => content ?? []);
    const totalTeachers = await getTeachers({ page: 0 }).then(({ totalElements }) => totalElements);
    const teachers = await getTeachers({ page: 0, size: totalTeachers }).then(({ content }) => content ?? []);
    const totalRooms = await getRooms({ page: 0 }).then(({ totalElements }) => totalElements);
    const rooms = await getRooms({ page: 0, size: totalRooms }).then(({ content }) => content ?? []);
    const totalGroups = await getStudentGroups({ page: 0 }).then(({ totalElements }) => totalElements);
    const groups = await getStudentGroups({ page: 0, size: totalGroups }).then(({ content }) => content ?? []);
    if (teachers.length > 0) {
      await deleteTeachers(teachers.map((teacher) => teacher.id));
    }
    if (students.length > 0) {
      await deleteStudents(students.map((student) => student.id));
    }
    if (rooms.length > 0) {
      await deleteRooms(rooms.map((room) => room.id));
    }
    if (groups.length > 0) {
      for (const group of groups) {
        await deleteStudentGroup(group.id);
      }
    }
    const grades = await getGrades({});
    if (grades.length > 0) {
      await deleteGrades(grades.map((grade) => grade.id));
    }
    await page.goto('/');
    await page.getByRole('link').first().click();
    await page.getByRole('link', { name: 'UCDL Editor' }).click();
    await expect(page).toHaveURL('/admin/editor');
    await expect(async () => {
      const fileChooserPromise = page.waitForEvent('filechooser', { timeout: 1000 });
      await page.getByText('Upload').click();
      const fileChooser = await fileChooserPromise;
      await fileChooser.setFiles('./tests/empty-ucdl.yml');
      await expect(page.locator('.view-line').first()).toBeEmpty({ timeout: 500 });
    }).toPass();
    await page.getByRole('button', { name: 'Speichern' }).click();
    if (await page.getByRole('heading', { name: 'Datei konnte nicht' }).isVisible()) {
      await page.getByRole('button', { name: 'Ja' }).click();
    }
    await page.goto('/');
    await page.getByRole('link').first().click();
    await expect(page).toHaveURL('/admin');
    await page.locator('div.gap-4:nth-child(1) > p:nth-child(1)').waitFor();
    await expect(page.getByText('0', { exact: true }).count()).resolves.toBe(5);
    for (let i = 0; i < 5; i++) {
      await createStudent({ firstName: `Con${i}`, lastName: `straint${i}`, tagIds: [] });
    }
    await createGrade({ name: 'grade', tagIds: [], studentGroupIds: [] });
    await createGrade({ name: 'grade', tagIds: [], studentGroupIds: [] });
    for (let i = 0; i < 5; i++) {
      await createTeacher({ firstName: `teach${i}`, lastName: `er${i}`, acronym: `a`, subjectIds: [], tagIds: [] });
    }
    await page.reload();
    await expect(page.getByText('0', { exact: true })).toHaveCount(2);
    await expect(page.getByText('5', { exact: true })).toHaveCount(2);
    await expect(page.getByText('2', { exact: true })).toHaveCount(1);
  });
});
