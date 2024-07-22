/**
 * UFTOS OpenAPI definition
 * v0
 * DO NOT MODIFY - This file has been generated using oazapfts.
 * See https://www.npmjs.com/package/oazapfts
 */
import * as Oazapfts from "@oazapfts/runtime";
import * as QS from "@oazapfts/runtime/query";
export const defaults: Oazapfts.Defaults<Oazapfts.CustomHeaders> = {
    headers: {},
    baseUrl: "http://localhost:5173/api",
};
const oazapfts = Oazapfts.runtime(defaults);
export const servers = {
    uftosApiUrl: "http://localhost:5173/api"
};
export type Pageable = {
    page?: number;
    size?: number;
    sort?: string[];
};
export type ConstraintParameter = {
    id: string;
    parameterName: string;
    parameterType: ParameterType;
};
export type ConstraintSignature = {
    defaultType: DefaultType;
    description: string;
    name: string;
    parameters: ConstraintParameter[];
};
export type SortObject = {
    ascending?: boolean;
    direction?: string;
    ignoreCase?: boolean;
    nullHandling?: string;
    property?: string;
};
export type PageableObject = {
    offset?: number;
    pageNumber?: number;
    pageSize?: number;
    paged?: boolean;
    sort?: SortObject[];
    unpaged?: boolean;
};
export type PageConstraintSignature = {
    content?: ConstraintSignature[];
    empty?: boolean;
    first?: boolean;
    last?: boolean;
    "number"?: number;
    numberOfElements?: number;
    pageable?: PageableObject;
    size?: number;
    sort?: SortObject[];
    totalElements?: number;
    totalPages?: number;
};
export type SlimArgument = {
    id: string;
    value: string;
};
export type SlimInstance = {
    arguments: SlimArgument[];
    id: string;
    "type": Type;
};
export type ConstraintArgumentDisplayName = {
    displayName: string;
    id: string;
};
export type ConstraintInstancesResponseDto = {
    constraintInstances: SlimInstance[];
    displayNames: ConstraintArgumentDisplayName[];
    parameters: ConstraintParameter[];
};
export type ConstraintInstanceRequestDto = {
    arguments: {
        [key: string]: string;
    };
    "type"?: Type;
};
export type ConstraintArgument = {
    constraintParameter: ConstraintParameter;
    id: string;
    value: string;
};
export type ConstraintInstance = {
    arguments: ConstraintArgument[];
    id: string;
    signature: ConstraintSignature;
    "type": Type;
};
export type Tag = {
    id: string;
    name: string;
};
export type GradeResponseDto = {
    id: string;
    name: string;
    studentGroupIds: string[];
    studentIds: string[];
    tags: Tag[];
};
export type Subject = {
    color?: string;
    id: string;
    name: string;
    tags: Tag[];
};
export type LessonsCount = {
    count?: number;
    id?: string;
    subject?: Subject;
};
export type CurriculumResponseDto = {
    grade: GradeResponseDto;
    id: string;
    lessonsCounts: LessonsCount[];
    name: string;
};
export type PageCurriculumResponseDto = {
    content?: CurriculumResponseDto[];
    empty?: boolean;
    first?: boolean;
    last?: boolean;
    "number"?: number;
    numberOfElements?: number;
    pageable?: PageableObject;
    size?: number;
    sort?: SortObject[];
    totalElements?: number;
    totalPages?: number;
};
export type LessonsCountRequestDto = {
    count: number;
    subjectId: string;
};
export type CurriculumRequestDto = {
    gradeId: string;
    lessonsCounts: LessonsCountRequestDto[];
    name: string;
};
export type ParsingResponse = {
    message?: string;
    success?: boolean;
};
export type Sort = {
    sort?: string[];
};
export type GradeRequestDto = {
    name: string;
    studentGroupIds: string[];
    tagIds: string[];
};
export type Timeslot = {
    day: Day;
    id: string;
    slot: number;
    tags: Tag[];
};
export type BulkLesson = {
    gradeIds: string[];
    id: string;
    index?: number;
    roomId: string;
    subjectId: string;
    teacherId: string;
    timeslot: Timeslot;
};
export type Room = {
    buildingName: string;
    capacity: number;
    id: string;
    name: string;
    tags: Tag[];
};
export type Teacher = {
    acronym: string;
    firstName: string;
    id: string;
    lastName: string;
    subjects: Subject[];
    tags: Tag[];
};
export type Timetable = {
    id: string;
    name: string;
};
export type LessonResponseDto = {
    grades: GradeResponseDto[];
    lessons: BulkLesson[];
    rooms: Room[];
    subjects: Subject[];
    teachers: Teacher[];
    timetable: Timetable;
};
export type PageLessonResponseDto = {
    content?: LessonResponseDto[];
    empty?: boolean;
    first?: boolean;
    last?: boolean;
    "number"?: number;
    numberOfElements?: number;
    pageable?: PageableObject;
    size?: number;
    sort?: SortObject[];
    totalElements?: number;
    totalPages?: number;
};
export type LessonRequestDto = {
    index: number;
    roomId: string;
    studentGroupId: string;
    subjectId: string;
    teacherId: string;
    timeslotId: string;
    timetableId: string;
};
export type Curriculum = {
    grade?: Grade;
    id?: string;
    lessonsCounts?: LessonsCount[];
    name: string;
};
export type Grade = {
    curricula?: Curriculum[];
    id?: string;
    name?: string;
    studentGroups?: StudentGroup[];
    tags?: Tag[];
};
export type Student = {
    firstName: string;
    id: string;
    lastName: string;
    tags: Tag[];
};
export type StudentGroup = {
    grades: Grade[];
    id: string;
    name: string;
    students: Student[];
    tags: Tag[];
};
export type Lesson = {
    id: string;
    index: number;
    room: Room;
    studentGroup: StudentGroup;
    subject: Subject;
    teacher: Teacher;
    timeslot: Timeslot;
    timetable: Timetable;
    year: string;
};
export type PageRoom = {
    content?: Room[];
    empty?: boolean;
    first?: boolean;
    last?: boolean;
    "number"?: number;
    numberOfElements?: number;
    pageable?: PageableObject;
    size?: number;
    sort?: SortObject[];
    totalElements?: number;
    totalPages?: number;
};
export type RoomRequestDto = {
    buildingName: string;
    capacity: number;
    name: string;
    tagIds: string[];
};
export type ServerStatisticsResponseDto = {
    classCount: number;
    resourceCount: number;
    roomCount: number;
    studentCount: number;
    teacherCount: number;
};
export type Break = {
    afterSlot: number;
    length: number;
    long?: boolean;
};
export type TimetableMetadata = {
    breaks: Break[];
    startTime: string;
    timeslotLength: number;
};
export type PageStudentGroup = {
    content?: StudentGroup[];
    empty?: boolean;
    first?: boolean;
    last?: boolean;
    "number"?: number;
    numberOfElements?: number;
    pageable?: PageableObject;
    size?: number;
    sort?: SortObject[];
    totalElements?: number;
    totalPages?: number;
};
export type StudentGroupRequestDto = {
    gradeIds: string[];
    name: string;
    studentIds: string[];
    tagIds: string[];
};
export type PageStudent = {
    content?: Student[];
    empty?: boolean;
    first?: boolean;
    last?: boolean;
    "number"?: number;
    numberOfElements?: number;
    pageable?: PageableObject;
    size?: number;
    sort?: SortObject[];
    totalElements?: number;
    totalPages?: number;
};
export type StudentRequestDto = {
    firstName: string;
    lastName: string;
    tagIds: string[];
};
export type SubjectRequestDto = {
    color?: string;
    name: string;
    tagIds: string[];
};
export type TagRequestDto = {
    tagName: string;
};
export type PageTeacher = {
    content?: Teacher[];
    empty?: boolean;
    first?: boolean;
    last?: boolean;
    "number"?: number;
    numberOfElements?: number;
    pageable?: PageableObject;
    size?: number;
    sort?: SortObject[];
    totalElements?: number;
    totalPages?: number;
};
export type TeacherRequestDto = {
    acronym: string;
    firstName: string;
    lastName: string;
    subjectIds: string[];
    tagIds: string[];
};
export type PageTimeslot = {
    content?: Timeslot[];
    empty?: boolean;
    first?: boolean;
    last?: boolean;
    "number"?: number;
    numberOfElements?: number;
    pageable?: PageableObject;
    size?: number;
    sort?: SortObject[];
    totalElements?: number;
    totalPages?: number;
};
export type TimeslotRequestDto = {
    day: Day;
    slot: number;
    tagIds: string[];
};
export type PageTimetable = {
    content?: Timetable[];
    empty?: boolean;
    first?: boolean;
    last?: boolean;
    "number"?: number;
    numberOfElements?: number;
    pageable?: PageableObject;
    size?: number;
    sort?: SortObject[];
    totalElements?: number;
    totalPages?: number;
};
export type TimetableRequestDto = {
    name: string;
};
export function getConstraintSignatures(pageable: Pageable, { name }: {
    name?: string;
} = {}, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: PageConstraintSignature;
    }>(`/constraints${QS.query(QS.explode({
        pageable,
        name
    }))}`, {
        ...opts
    }));
}
export function getConstraintSignature(signatureId: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: ConstraintSignature;
    }>(`/constraints/${encodeURIComponent(signatureId)}`, {
        ...opts
    }));
}
export function getConstraintInstances(signatureId: string, pageable: Pageable, { argument }: {
    argument?: string;
} = {}, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: ConstraintInstancesResponseDto;
    }>(`/constraints/${encodeURIComponent(signatureId)}/instances${QS.query(QS.explode({
        pageable,
        argument
    }))}`, {
        ...opts
    }));
}
export function createConstraintInstance(signatureId: string, request: ConstraintInstanceRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: ConstraintInstance;
    }>(`/constraints/${encodeURIComponent(signatureId)}/instances${QS.query(QS.explode({
        request
    }))}`, {
        ...opts,
        method: "POST"
    }));
}
export function deleteConstraintInstance(signatureId: string, id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchText(`/constraints/${encodeURIComponent(signatureId)}/instances/${encodeURIComponent(id)}`, {
        ...opts,
        method: "DELETE"
    }));
}
export function getConstraintInstanceById(signatureId: string, id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: ConstraintInstancesResponseDto;
    }>(`/constraints/${encodeURIComponent(signatureId)}/instances/${encodeURIComponent(id)}`, {
        ...opts
    }));
}
export function updateConstraintInstanceById(signatureId: string, id: string, request: ConstraintInstanceRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: ConstraintInstance;
    }>(`/constraints/${encodeURIComponent(signatureId)}/instances/${encodeURIComponent(id)}${QS.query(QS.explode({
        request
    }))}`, {
        ...opts,
        method: "PUT"
    }));
}
export function getCurriculums(pageable: Pageable, { name }: {
    name?: string;
} = {}, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: PageCurriculumResponseDto;
    }>(`/curriculum${QS.query(QS.explode({
        pageable,
        name
    }))}`, {
        ...opts
    }));
}
export function createCurriculum(curriculumRequestDto: CurriculumRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: CurriculumResponseDto;
    }>("/curriculum", oazapfts.json({
        ...opts,
        method: "POST",
        body: curriculumRequestDto
    })));
}
export function deleteCurriculum(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchText(`/curriculum/${encodeURIComponent(id)}`, {
        ...opts,
        method: "DELETE"
    }));
}
export function getCurriculum(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: CurriculumResponseDto;
    }>(`/curriculum/${encodeURIComponent(id)}`, {
        ...opts
    }));
}
export function updateCurriculum(id: string, curriculumRequestDto: CurriculumRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: CurriculumResponseDto;
    }>(`/curriculum/${encodeURIComponent(id)}`, oazapfts.json({
        ...opts,
        method: "PUT",
        body: curriculumRequestDto
    })));
}
export function getUcdlFile(opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Blob;
    }>("/editor", {
        ...opts
    }));
}
export function setUcdlFile(body: {
    file?: Blob;
}, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: ParsingResponse;
    }>("/editor", oazapfts.json({
        ...opts,
        method: "PUT",
        body
    })));
}
export function getGrades(sort: Sort, { name }: {
    name?: string;
} = {}, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: GradeResponseDto[];
    }>(`/grades${QS.query(QS.explode({
        sort,
        name
    }))}`, {
        ...opts
    }));
}
export function createGrade(gradeRequestDto: GradeRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: GradeResponseDto;
    }>("/grades", oazapfts.json({
        ...opts,
        method: "POST",
        body: gradeRequestDto
    })));
}
export function deleteGrade(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchText(`/grades/${encodeURIComponent(id)}`, {
        ...opts,
        method: "DELETE"
    }));
}
export function getGrade(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: GradeResponseDto;
    }>(`/grades/${encodeURIComponent(id)}`, {
        ...opts
    }));
}
export function updateGrade(id: string, gradeRequestDto: GradeRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: GradeResponseDto;
    }>(`/grades/${encodeURIComponent(id)}`, oazapfts.json({
        ...opts,
        method: "PUT",
        body: gradeRequestDto
    })));
}
export function getGradeLessons(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: LessonResponseDto[];
    }>(`/grades/${encodeURIComponent(id)}/lessons`, {
        ...opts
    }));
}
export function getLessons(pageable: Pageable, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: PageLessonResponseDto;
    }>(`/lessons${QS.query(QS.explode({
        pageable
    }))}`, {
        ...opts
    }));
}
export function createLesson(lessonRequestDto: LessonRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Lesson;
    }>("/lessons", oazapfts.json({
        ...opts,
        method: "POST",
        body: lessonRequestDto
    })));
}
export function deleteLesson(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchText(`/lessons/${encodeURIComponent(id)}`, {
        ...opts,
        method: "DELETE"
    }));
}
export function getLesson(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Lesson;
    }>(`/lessons/${encodeURIComponent(id)}`, {
        ...opts
    }));
}
export function updateLesson(id: string, lessonRequestDto: LessonRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Lesson;
    }>(`/lessons/${encodeURIComponent(id)}`, oazapfts.json({
        ...opts,
        method: "PUT",
        body: lessonRequestDto
    })));
}
export function getRooms(pageable: Pageable, { name, buildingName, capacity, tags }: {
    name?: string;
    buildingName?: string;
    capacity?: number;
    tags?: string[];
} = {}, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: PageRoom;
    }>(`/rooms${QS.query(QS.explode({
        pageable,
        name,
        buildingName,
        capacity,
        tags
    }))}`, {
        ...opts
    }));
}
export function createRoom(roomRequestDto: RoomRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Room;
    }>("/rooms", oazapfts.json({
        ...opts,
        method: "POST",
        body: roomRequestDto
    })));
}
export function deleteRoom(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchText(`/rooms/${encodeURIComponent(id)}`, {
        ...opts,
        method: "DELETE"
    }));
}
export function getRoom(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Room;
    }>(`/rooms/${encodeURIComponent(id)}`, {
        ...opts
    }));
}
export function updateRoom(id: string, roomRequestDto: RoomRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Room;
    }>(`/rooms/${encodeURIComponent(id)}`, oazapfts.json({
        ...opts,
        method: "PUT",
        body: roomRequestDto
    })));
}
export function getRoomLessons(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: LessonResponseDto[];
    }>(`/rooms/${encodeURIComponent(id)}/lessons`, {
        ...opts
    }));
}
export function getServerStats(opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: ServerStatisticsResponseDto;
    }>("/server/statistics", {
        ...opts
    }));
}
export function getTimetableMetadata(opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: TimetableMetadata;
    }>("/server/timetable-metadata", {
        ...opts
    }));
}
export function setTimetableMetadata(timetableMetadata: TimetableMetadata, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchText(`/server/timetable-metadata${QS.query(QS.explode({
        timetableMetadata
    }))}`, {
        ...opts,
        method: "PUT"
    }));
}
export function getStudentGroups(pageable: Pageable, { name }: {
    name?: string;
} = {}, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: PageStudentGroup;
    }>(`/student-groups${QS.query(QS.explode({
        pageable,
        name
    }))}`, {
        ...opts
    }));
}
export function createStudentGroup(studentGroupRequestDto: StudentGroupRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: StudentGroup;
    }>("/student-groups", oazapfts.json({
        ...opts,
        method: "POST",
        body: studentGroupRequestDto
    })));
}
export function deleteStudentGroup(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchText(`/student-groups/${encodeURIComponent(id)}`, {
        ...opts,
        method: "DELETE"
    }));
}
export function getStudentGroup(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: StudentGroup;
    }>(`/student-groups/${encodeURIComponent(id)}`, {
        ...opts
    }));
}
export function updateStudentGroup(id: string, studentGroupRequestDto: StudentGroupRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: StudentGroup;
    }>(`/student-groups/${encodeURIComponent(id)}`, oazapfts.json({
        ...opts,
        method: "PUT",
        body: studentGroupRequestDto
    })));
}
export function getStudentGroupLessons(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: LessonResponseDto[];
    }>(`/student-groups/${encodeURIComponent(id)}/lessons`, {
        ...opts
    }));
}
export function removeStudentsFromStudentGroup(id: string, body: string[], opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchText(`/student-groups/${encodeURIComponent(id)}/students`, oazapfts.json({
        ...opts,
        method: "DELETE",
        body
    })));
}
export function addStudentsToStudentGroup(id: string, body: string[], opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: StudentGroup;
    }>(`/student-groups/${encodeURIComponent(id)}/students`, oazapfts.json({
        ...opts,
        method: "POST",
        body
    })));
}
export function getStudents(pageable: Pageable, { firstName, lastName, tags }: {
    firstName?: string;
    lastName?: string;
    tags?: string[];
} = {}, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: PageStudent;
    }>(`/students${QS.query(QS.explode({
        pageable,
        firstName,
        lastName,
        tags
    }))}`, {
        ...opts
    }));
}
export function createStudent(studentRequestDto: StudentRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Student;
    }>("/students", oazapfts.json({
        ...opts,
        method: "POST",
        body: studentRequestDto
    })));
}
export function deleteStudent(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchText(`/students/${encodeURIComponent(id)}`, {
        ...opts,
        method: "DELETE"
    }));
}
export function getStudent(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Student;
    }>(`/students/${encodeURIComponent(id)}`, {
        ...opts
    }));
}
export function updateStudent(id: string, studentRequestDto: StudentRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Student;
    }>(`/students/${encodeURIComponent(id)}`, oazapfts.json({
        ...opts,
        method: "PUT",
        body: studentRequestDto
    })));
}
export function getSubjects(sort: Sort, { name }: {
    name?: string;
} = {}, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Subject[];
    }>(`/subjects${QS.query(QS.explode({
        sort,
        name
    }))}`, {
        ...opts
    }));
}
export function createSubject(subjectRequestDto: SubjectRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Subject;
    }>("/subjects", oazapfts.json({
        ...opts,
        method: "POST",
        body: subjectRequestDto
    })));
}
export function deleteSubject(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchText(`/subjects/${encodeURIComponent(id)}`, {
        ...opts,
        method: "DELETE"
    }));
}
export function getSubject(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Subject;
    }>(`/subjects/${encodeURIComponent(id)}`, {
        ...opts
    }));
}
export function updateSubject(id: string, subjectRequestDto: SubjectRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Subject;
    }>(`/subjects/${encodeURIComponent(id)}`, oazapfts.json({
        ...opts,
        method: "PUT",
        body: subjectRequestDto
    })));
}
export function getTags(sort: Sort, { name }: {
    name?: string;
} = {}, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Tag[];
    }>(`/tags${QS.query(QS.explode({
        sort,
        name
    }))}`, {
        ...opts
    }));
}
export function createTag(tagRequestDto: TagRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Tag;
    }>("/tags", oazapfts.json({
        ...opts,
        method: "POST",
        body: tagRequestDto
    })));
}
export function deleteTag(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchText(`/tags/${encodeURIComponent(id)}`, {
        ...opts,
        method: "DELETE"
    }));
}
export function getTag(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Tag;
    }>(`/tags/${encodeURIComponent(id)}`, {
        ...opts
    }));
}
export function updateTag(id: string, tagRequestDto: TagRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Tag;
    }>(`/tags/${encodeURIComponent(id)}`, oazapfts.json({
        ...opts,
        method: "PUT",
        body: tagRequestDto
    })));
}
export function getTeachers(pageable: Pageable, { firstName, lastName, acronym, subjects, tags }: {
    firstName?: string;
    lastName?: string;
    acronym?: string;
    subjects?: string[];
    tags?: string[];
} = {}, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: PageTeacher;
    }>(`/teachers${QS.query(QS.explode({
        pageable,
        firstName,
        lastName,
        acronym,
        subjects,
        tags
    }))}`, {
        ...opts
    }));
}
export function createTeacher(teacherRequestDto: TeacherRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Teacher;
    }>("/teachers", oazapfts.json({
        ...opts,
        method: "POST",
        body: teacherRequestDto
    })));
}
export function deleteTeacher(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchText(`/teachers/${encodeURIComponent(id)}`, {
        ...opts,
        method: "DELETE"
    }));
}
export function getTeacher(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Teacher;
    }>(`/teachers/${encodeURIComponent(id)}`, {
        ...opts
    }));
}
export function updateTeacher(id: string, teacherRequestDto: TeacherRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Teacher;
    }>(`/teachers/${encodeURIComponent(id)}`, oazapfts.json({
        ...opts,
        method: "PUT",
        body: teacherRequestDto
    })));
}
export function getTeacherLessons(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: LessonResponseDto;
    }>(`/teachers/${encodeURIComponent(id)}/lessons`, {
        ...opts
    }));
}
export function getTimeslots(pageable: Pageable, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: PageTimeslot;
    }>(`/timeslots${QS.query(QS.explode({
        pageable
    }))}`, {
        ...opts
    }));
}
export function createTimeslot(timeslotRequestDto: TimeslotRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Timeslot;
    }>("/timeslots", oazapfts.json({
        ...opts,
        method: "POST",
        body: timeslotRequestDto
    })));
}
export function deleteTimeslot(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchText(`/timeslots/${encodeURIComponent(id)}`, {
        ...opts,
        method: "DELETE"
    }));
}
export function getTimeslot(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Timeslot;
    }>(`/timeslots/${encodeURIComponent(id)}`, {
        ...opts
    }));
}
export function updateTimeslot(id: string, timeslotRequestDto: TimeslotRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Timeslot;
    }>(`/timeslots/${encodeURIComponent(id)}`, oazapfts.json({
        ...opts,
        method: "PUT",
        body: timeslotRequestDto
    })));
}
export function getTimetables(pageable: Pageable, { name }: {
    name?: string;
} = {}, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: PageTimetable;
    }>(`/timetables${QS.query(QS.explode({
        pageable,
        name
    }))}`, {
        ...opts
    }));
}
export function createTimetable(timetableRequestDto: TimetableRequestDto, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Timetable;
    }>("/timetables", oazapfts.json({
        ...opts,
        method: "POST",
        body: timetableRequestDto
    })));
}
export function deleteTimetable(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchText(`/timetables/${encodeURIComponent(id)}`, {
        ...opts,
        method: "DELETE"
    }));
}
export function getTimetable(id: string, opts?: Oazapfts.RequestOpts) {
    return oazapfts.ok(oazapfts.fetchJson<{
        status: 200;
        data: Timetable;
    }>(`/timetables/${encodeURIComponent(id)}`, {
        ...opts
    }));
}
export enum DefaultType {
    SoftReward = "SOFT_REWARD",
    HardReward = "HARD_REWARD",
    SoftPenalize = "SOFT_PENALIZE",
    HardPenalize = "HARD_PENALIZE"
}
export enum ParameterType {
    Grade = "GRADE",
    Lesson = "LESSON",
    Room = "ROOM",
    Student = "STUDENT",
    StudentGroup = "STUDENT_GROUP",
    Subject = "SUBJECT",
    Tag = "TAG",
    Teacher = "TEACHER",
    Timeslot = "TIMESLOT",
    Number = "NUMBER",
    Timetable = "TIMETABLE"
}
export enum Type {
    SoftReward = "SOFT_REWARD",
    HardReward = "HARD_REWARD",
    SoftPenalize = "SOFT_PENALIZE",
    HardPenalize = "HARD_PENALIZE"
}
export enum Day {
    Monday = "MONDAY",
    Tuesday = "TUESDAY",
    Wednesday = "WEDNESDAY",
    Thursday = "THURSDAY",
    Friday = "FRIDAY",
    Saturday = "SATURDAY",
    Sunday = "SUNDAY"
}
