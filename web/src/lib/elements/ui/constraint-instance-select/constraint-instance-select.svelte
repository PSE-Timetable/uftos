<script lang="ts">
    import * as Select from '$lib/elements/ui/select';
    import {
        type ConstraintParameter,
        type Grade,
        type Room,
        type StudentGroup, type Subject,
        type Tag,
        type Teacher,
        type Timeslot
    } from "$lib/sdk/fetch-client";

    export let teachers: Teacher[] = [];
    export let rooms: Room[] = [];
    export let grades: Grade[] = [];
    export let tags: Tag[] = [];
    export let timeslots: Timeslot[] = [];
    export let studentGroups: StudentGroup[] = [];
    export let subjects: Subject[] = [];
    export let constraintParameter: ConstraintParameter;

    let items: { value: string, label: string }[] = [];

    function push(text: string) {
        items.push({value: text, label: text})
    }

    if (teachers.length > 0) {
        for (const teacher of teachers) {
            push(teacher.firstName + ' ' + teacher.lastName);
        }
    } else if (rooms.length > 0) {
        for (const room of rooms) {
            push(room.name);
        }
    } else if (grades.length > 0) {
        for (const grade of grades) {
            push(grade.name || "Not found");
        }
    } else if (timeslots.length > 0) {
        for (const timeslot of timeslots) {
            push(timeslot.day + ' ' + timeslot.slot);
        }
    } else if (studentGroups.length > 0) {
        for (const studentGroup of studentGroups) {
            push(studentGroup.name);
        }
    } else if (subjects.length > 0) {
        for (const subject of subjects) {
            push(subject.name);
        }
    } else if (tags.length > 0) {
        for (const tag of tags) {
            push(tag.name);
        }
    }
</script>

<div class="flex flex-row items-center justify-around w-full gap-8">
    <p>{ constraintParameter.parameterName }</p>
    <div class="text-primary">
        <Select.Root portal={null}>
            <Select.Trigger class="w-[10%] min-w-[200px]">
                <Select.Value placeholder="Wähle etwas aus" />
            </Select.Trigger>
            <Select.Content>
                <Select.Group>
                    {#each items as item}
                        <Select.Item value={item.value} label={item.label}>
                            {item.label}
                        </Select.Item>
                    {/each}
                </Select.Group>
            </Select.Content>
            <Select.Input name="chosenResource" />
        </Select.Root>
    </div>
</div>