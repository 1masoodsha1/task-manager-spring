import { useEffect, useMemo, useState } from 'react';
import './TaskForm.css';
import type { Task, TaskCreateOrUpdate, TaskStatus } from '../../models/task';

type Props = {
  initialTask: Task | null;
  onSubmit: (task: TaskCreateOrUpdate) => void;
  onCancel: () => void;
};

const TITLE_MAX_LENGTH = 255;

export default function TaskForm({ initialTask, onSubmit, onCancel }: Props) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [status, setStatus] = useState<TaskStatus>('TODO');
  const [dueDate, setDueDate] = useState('');

  useEffect(() => {
    if (initialTask) {
      setTitle(initialTask.title ?? '');
      setDescription(initialTask.description ?? '');
      setStatus(initialTask.status ?? 'TODO');
      setDueDate(initialTask.dueDate ? initialTask.dueDate.slice(0, 16) : '');
    } else {
      setTitle('');
      setDescription('');
      setStatus('TODO');
      setDueDate('');
    }
  }, [initialTask]);

  const hasBlankTitleError = useMemo(() => !title.trim(), [title]);
  const hasTitleTooLongError = useMemo(
    () => title.length > TITLE_MAX_LENGTH,
    [title],
  );

  const hasPastDueDateError = useMemo(() => {
    if (!dueDate || status === 'DONE') return false;
    return new Date(dueDate).getTime() < new Date().getTime();
  }, [dueDate, status]);

  function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();

    const cleanTitle = title.trim();
    if (!cleanTitle || hasTitleTooLongError || hasPastDueDateError) return;

    onSubmit({
      title: cleanTitle,
      description: description.trim() ? description.trim() : null,
      status,
      dueDate: dueDate ? new Date(dueDate).toISOString() : null,
    });

    if (!initialTask) {
      setTitle('');
      setDescription('');
      setStatus('TODO');
      setDueDate('');
    }
  }

  return (
    <form onSubmit={handleSubmit} className="task-form">
      <div>
        <label htmlFor="title">Title</label>
        <input
          id="title"
          type="text"
          value={title}
          maxLength={TITLE_MAX_LENGTH}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Enter task title"
        />
        {hasBlankTitleError && <div className="error">Title is required</div>}
        {hasTitleTooLongError && (
          <div className="error">Title must be 255 characters or fewer</div>
        )}
      </div>

      <div>
        <label htmlFor="description">Description</label>
        <textarea
          id="description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
      </div>

      <div>
        <label htmlFor="status">Status</label>
        <select
          id="status"
          value={status}
          onChange={(e) => setStatus(e.target.value as TaskStatus)}
        >
          <option value="TODO">TODO</option>
          <option value="IN_PROGRESS">IN_PROGRESS</option>
          <option value="DONE">DONE</option>
        </select>
      </div>

      <div>
        <label htmlFor="dueDate">Due date &amp; time</label>
        <input
          id="dueDate"
          type="datetime-local"
          value={dueDate}
          onChange={(e) => setDueDate(e.target.value)}
        />
        {hasPastDueDateError && (
          <div className="error">
            Past due date/time is only allowed for completed tasks.
          </div>
        )}
      </div>

      <div className="buttons">
        <button
          type="submit"
          disabled={
            hasBlankTitleError || hasTitleTooLongError || hasPastDueDateError
          }
        >
          Save
        </button>
        <button type="button" onClick={onCancel}>
          Cancel
        </button>
      </div>
    </form>
  );
}