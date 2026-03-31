import './TaskList.css';
import type { Task } from '../models/task';

interface TaskListProps {
  tasks: Task[];
  loading: boolean;
  onEdit: (task: Task) => void;
  onDelete: (id: number) => void;
  onRefresh: () => void;
}

function isOverdue(task: Task): boolean {
  if (!task.dueDate || task.status === 'DONE') return false;
  return new Date(task.dueDate).getTime() < new Date().getTime();
}

export default function TaskList({
  tasks,
  loading,
  onEdit,
  onDelete,
  onRefresh,
}: TaskListProps) {
  return (
    <div className="task-list">
      <div className="task-list-header">
        <h3>Tasks</h3>
        <button onClick={onRefresh} disabled={loading}>
          Refresh
        </button>
      </div>

      {loading ? (
        <div>Loading...</div>
      ) : tasks.length === 0 ? (
        <div>No tasks yet</div>
      ) : (
        <ul>
          {tasks.map((task) => (
            <li key={task.id}>
              <div className="task-list-row">
                <div className="task-list-main">
                  <strong>{task.title}</strong>
                  <div className="task-list-meta">
                    <span>{task.status}</span>
                    {isOverdue(task) && <span className="task-list-overdue"> • OVERDUE</span>}
                    {task.dueDate && (
                      <span> • due {new Date(task.dueDate).toLocaleString()}</span>
                    )}
                  </div>

                  {task.description && (
                    <div className="task-list-desc">{task.description}</div>
                  )}
                </div>

                <div className="task-list-actions">
                  <button onClick={() => onEdit(task)}>Edit</button>
                  <button
                    onClick={() => {
                      if (window.confirm('Delete task?')) onDelete(task.id);
                    }}
                  >
                    Delete
                  </button>
                </div>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}