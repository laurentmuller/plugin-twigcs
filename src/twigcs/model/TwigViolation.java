/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.model;

import java.util.Objects;

import org.eclipse.core.resources.IMarker;

import twigcs.core.ResourceText;

/**
 * Represents a single file violation.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigViolation implements Comparable<TwigViolation> {

	/*
	 * the line offset (zero index based)
	 */
	private int line;

	/*
	 * the column offset (zero index based)
	 */
	private int column;
	/*
	 * the severity
	 */
	private TwigSeverity severity;

	/*
	 * the message
	 */
	private String message;

	/**
	 * Creates a new instance of this class.
	 */
	public TwigViolation() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(final TwigViolation o) {
		int result = Integer.compare(line, o.line);
		if (result == 0) {
			result = Integer.compare(column, o.column);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TwigViolation other = (TwigViolation) obj;
		return column == other.column && line == other.line;
	}

	/**
	 * Gets the column offset (zero index based).
	 *
	 * @return the column offset.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Gets the line offset (zero index based).
	 *
	 * @return the line offset.
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Gets the marker severity.
	 *
	 * @return the marker severity, if any; {@link IMarker#SEVERITY_INFO}
	 *         otherwise.
	 */
	public int getMarkerSeverity() {
		if (severity != null) {
			return severity.getMarkerSeverity();
		} else {
			return IMarker.SEVERITY_INFO;
		}
	}

	/**
	 * Gets the message.
	 *
	 * @return the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gets the start violation offset.
	 * 
	 * @param text
	 *            the text to get the line offset.
	 * @return the start violation offset.
	 */
	public int getOffset(final ResourceText text) {
		return text.getOffset(line - 1) + column;
	}

	/**
	 * Gets the severity.
	 *
	 * @return the severity.
	 */
	public TwigSeverity getSeverity() {
		return severity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(column, line);
	}

	/**
	 * Set the column offset (zero index based).
	 *
	 * @param column
	 *            the column offset to set.
	 */
	public void setColumn(final int column) {
		this.column = column;
	}

	/**
	 * Set the line offset (zero index based).
	 *
	 * @param column
	 *            the line offset to set.
	 */
	public void setLine(final int line) {
		this.line = line;
	}

	/**
	 * Sets the message.
	 *
	 * @param message
	 *            the message to set.
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

	/**
	 * Sets the severity.
	 *
	 * @param severity
	 *            the severity to set.
	 */
	public void setSeverity(final TwigSeverity severity) {
		this.severity = severity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final String name = getClass().getSimpleName();
		return String.format(
				"%s{line: %d, column: %d, severity: %s, message: \"%s\"}", // $NON-NLS-1$
				name, line, column, severity, message);
	}
}
